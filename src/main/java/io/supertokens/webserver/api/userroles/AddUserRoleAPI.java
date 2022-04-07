/*
 *    Copyright (c) 2022, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 */

package io.supertokens.webserver.api.userroles;

import com.google.gson.JsonObject;
import io.supertokens.Main;
import io.supertokens.inmemorydb.queries.UserRoleQueries;
import io.supertokens.pluginInterface.RECIPE_ID;
import io.supertokens.pluginInterface.exceptions.StorageQueryException;
import io.supertokens.pluginInterface.userroles.exception.UnknownRoleException;
import io.supertokens.userroles.UserRoles;
import io.supertokens.webserver.InputParser;
import io.supertokens.webserver.WebserverAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

public class AddUserRoleAPI extends WebserverAPI {

    @Serial
    private static final long serialVersionUID = -1244515701412497039L;

    public AddUserRoleAPI(Main main) {
        super(main, RECIPE_ID.USER_ROLES.toString());
    }

    @Override
    public String getPath() {
        return "/recipe/user/role";
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonObject input = InputParser.parseJsonObjectOrThrowError(req);

        String userId = InputParser.parseStringOrThrowError(input, "userId", false);

        String role = InputParser.parseStringFromElementOrThrowError(input, "role", false);

        // normalize and sanitize role
        role = normalizeAndSanitizeString(role, "role");

        try {
            UserRoles.addRoleToUser(main, userId, role);
            JsonObject response = new JsonObject();
            response.addProperty("status", "OK");
            super.sendJsonResponse(200, response, resp);
        } catch (UnknownRoleException e) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "UNKNOWN_ROLE_ERROR");
            super.sendJsonResponse(200, response, resp);
        } catch (StorageQueryException e) {
            throw new ServletException(e);
        }
    }

    private static String normalizeAndSanitizeString(String field, String fieldName) throws ServletException {
        String trimmedString = field.trim();

        if (trimmedString.length() == 0) {
            throw new ServletException(
                    new WebserverAPI.BadRequestException("Field name '" + fieldName + "' is invalid in JSON input"));
        }
        return trimmedString;
    }
}