package iddqd_idkfa.transfer.api;

import iddqd_idkfa.transfer.model.Account;
import iddqd_idkfa.transfer.model.OperationState;
import iddqd_idkfa.transfer.model.Transfer;
import iddqd_idkfa.transfer.repository.Repository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.TimeUnit;

public class TransferAPI extends AbstractVerticle {

    private int port;

    public TransferAPI(int port) {
        super();
        this.port = port;
    }

    @Override
    public void start(Future<Void> promise) {

        Router router = Router.router(vertx);

        router.route("/").handler(TransferAPI::returnWelcomeMessage);

        router.route().handler(BodyHandler.create());

        //routes for transfers
        router.get("/transfers").handler(this::fetchAllTransfers);
        router.get("/transfers/:id").handler(this::fetchTransfer);
        router.post("/transfers").handler(this::newTransfer);
        router.put("/transfers/:id").handler(this::runTransfer);

        //routes for accounts
        router.get("/accounts").handler(this::fetchAllAccounts);
        router.get("/accounts/:id").handler(this::fetchAccount);
        router.post("/accounts").handler(this::newAccount);
        router.put("/accounts/:id").handler(this::editAccount);
        router.delete("/accounts/:id").handler(this::removeAccount);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        this.port,
                        result -> {
                            if (result.succeeded()) {
                                promise.complete();
                            } else {
                                System.err.println(result.cause().toString());
                                promise.fail(result.cause());
                            }
                        }
                );
    }

    //getters and setters

    public int getPort() {
        return port;
    }

    // Response Utils - start

    private static void returnWelcomeMessage(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "text/html").end("transfer api - welcome");
    }

    private static void returnJsonResponse(RoutingContext ctx, Object responseObj) {
        returnJsonResponse(ctx, responseObj, 200);
    }

    private static void returnJsonResponse(RoutingContext ctx, Object responseObj, int statusCode) {
        ctx.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(responseObj));
    }

    private static void returnPlainResponse(RoutingContext ctx, int statusCode) {
        ctx.response().setStatusCode(statusCode).end();
    }

    // Response utils - end

    //Transfer Api Endpoints - Start

    private void fetchAllTransfers(RoutingContext routingContext) {
        returnJsonResponse(routingContext, Repository.allTransfers());
    }

    private void fetchTransfer(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            returnPlainResponse(routingContext, 400);
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Transfer transfer = Repository.getTransfer(idAsInteger);
            if (transfer == null) {
                returnPlainResponse(routingContext, 404);
            } else {
                returnJsonResponse(routingContext, transfer);
            }
        }
    }

    private void newTransfer(RoutingContext routingContext) {
        try {
            final Transfer transfer = Json.decodeValue(routingContext.getBodyAsString(),
                    Transfer.class);
            Repository.addTransfer(transfer.getId(), transfer);
            returnJsonResponse(routingContext, transfer, 201);
        } catch (Exception e) {
            returnPlainResponse(routingContext, 400);
        }
    }

    private void runTransfer(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            returnPlainResponse(routingContext, 400);
        } else {
            Account from = null;
            Account to = null;
            try {
                final Integer idAsInteger = Integer.valueOf(id);
                Transfer transfer = Repository.getTransfer(idAsInteger);
                if (transfer == null) {
                    returnPlainResponse(routingContext, 404);
                    return;
                }
                OperationState status = transfer.getStatus();
                BigDecimal amount = transfer.getAmount();
                from = Repository.getAccount(transfer.getFrom());
                to = Repository.getAccount(transfer.getTo());
                if( from == null || to == null) {
                    returnPlainResponse(routingContext, 404);
                    return;
                }
                boolean isLockedFrom = Repository.tryLock(from.getId());
                boolean isLockedTo = Repository.tryLock(to.getId());
                if (!isLockedFrom || !isLockedTo) {
                    returnPlainResponse(routingContext, 423);
                } else {
                    if (status != OperationState.SUCCESS &&
                            status != OperationState.FAIL &&
                            amount.compareTo(BigDecimal.ZERO) > 0 &&
                            from.getBalance().compareTo(transfer.getAmount()) >= 0 &&
                            from.getCurrency().equals(to.getCurrency()) &&
                            from.getCurrency().equals(transfer.getCurrency()) &&
                            to.getCurrency().equals(transfer.getCurrency())) {
                        from.withdraw(transfer.getAmount());
                        to.deposit(transfer.getAmount());
                        transfer.setStatus(OperationState.SUCCESS);
                    } else {
                        transfer.setStatus(OperationState.FAIL);
                    }
                    returnJsonResponse(routingContext, transfer);
                }
            } catch(Exception e) {
                System.out.println("error handling transfer request");
                e.printStackTrace();
                returnPlainResponse(routingContext, 500);
            } finally {
                if(from != null)
                    Repository.unlock(from.getId());
                if(to != null)
                    Repository.unlock(to.getId());
            }
        }
    }

    //Transfer Api endpoints - End


    // Account Api Endpoints - start

    private void fetchAllAccounts(RoutingContext routingContext) {
        returnJsonResponse(routingContext, Repository.allAccounts());
    }

    private void fetchAccount(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            returnPlainResponse(routingContext, 400);
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Account account = Repository.getAccount(idAsInteger);
            if (account == null) {
                returnPlainResponse(routingContext, 404);
            } else {
                returnJsonResponse(routingContext, account);
            }
        }
    }

    private void newAccount(RoutingContext routingContext) {
        try {
            final Account account = Json.decodeValue(routingContext.getBodyAsString(),
                    Account.class);
            Repository.addAccount(account.getId(), account);
            returnJsonResponse(routingContext, account, 201);
        } catch (Exception e) {
            returnPlainResponse(routingContext, 400);
        }
    }

    private void editAccount(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            returnPlainResponse(routingContext, 400);
        } else {
            final Integer intId = Integer.valueOf(id);
            Account account = Repository.getAccount(intId);
            if (account == null) {
                returnPlainResponse(routingContext, 404);
            } else {
                boolean updated = false;
                if (json.getString("name") != null && !json.getString("name").isEmpty()) {
                    account.setName(json.getString("name"));
                    updated = true;
                }
                if (json.getString("balance") != null && !json.getString("balance").isEmpty() &&
                        (new BigDecimal(json.getString("balance"))).compareTo(BigDecimal.ZERO) >= 0) {
                    account.setBalance(new BigDecimal(json.getString("balance")));
                    updated = true;
                }
                if (json.getString("currency") != null && !json.getString("currency").isEmpty()) {
                    try {
                        account.setCurrency(Currency.getInstance(json.getString("currency")));
                        updated = true;
                    } catch (Exception e) {
                        updated = false;
                    }
                }
                if (!updated) {
                    returnPlainResponse(routingContext, 400);
                } else {
                    returnJsonResponse(routingContext, account);
                }
            }
        }
    }

    private void removeAccount(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            returnPlainResponse(routingContext, 400);
        } else if (Repository.getAccount(Integer.valueOf(id)) == null) {
            returnPlainResponse(routingContext, 404);
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            Repository.removeAccount(idAsInteger);
            returnPlainResponse(routingContext, 204);
        }
    }

    //Account Api Endpoints - End

}
