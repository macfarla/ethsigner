/*
 * Copyright 2020 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.ethsigner.tests.tls.support;

import tech.pegasys.ethsigner.core.http.HttpResponseFactory;
import tech.pegasys.ethsigner.core.jsonrpc.JsonRpcRequest;
import tech.pegasys.ethsigner.core.jsonrpc.response.JsonRpcSuccessResponse;
import tech.pegasys.ethsigner.core.requesthandler.JsonRpcRequestHandler;

import io.vertx.ext.web.RoutingContext;

public class MockBalanceReporter implements JsonRpcRequestHandler {

  public static final int REPORTED_BALANCE = 300;

  @Override
  public void handle(final RoutingContext context, final JsonRpcRequest rpcRequest) {
    final HttpResponseFactory responseFactory = new HttpResponseFactory();
    responseFactory.create(
        context.request(),
        200,
        new JsonRpcSuccessResponse(rpcRequest.getId(), String.format("0x%X", REPORTED_BALANCE)));
  }
}
