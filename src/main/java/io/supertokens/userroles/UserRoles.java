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

package io.supertokens.userroles;

import io.supertokens.Main;
import io.supertokens.pluginInterface.exceptions.StorageQueryException;
import io.supertokens.pluginInterface.exceptions.StorageTransactionLogicException;
import io.supertokens.pluginInterface.userroles.exception.DuplicateRoleException;
import io.supertokens.pluginInterface.userroles.exception.DuplicateRolePermissionMappingException;
import io.supertokens.pluginInterface.userroles.exception.DuplicateUserRoleMappingException;
import io.supertokens.pluginInterface.userroles.exception.UnknownRoleException;
import io.supertokens.pluginInterface.userroles.sqlStorage.UserRolesSQLStorage;
import io.supertokens.storageLayer.StorageLayer;

import javax.annotation.Nullable;

public class UserRoles {
    // add a role to a user, if the role is already mapped to the user ignore the exception but if
    // the role does not exist, throw an UNKNOWN_ROLE_EXCEPTION error
    public static void addRoleToUser(Main main, String userId, String role)
            throws StorageQueryException, UnknownRoleException {
        try {
            StorageLayer.getUserRolesStorage(main).addRoleToUser(userId, role);
        } catch (DuplicateUserRoleMappingException e) {
            // ignore DuplicateUserRoleMappingException
        }
    }

    // create a new role if it doesn't exist and add permissions to the role
    public static void setRole(Main main, String role, String[] permissions)
            throws StorageQueryException, StorageTransactionLogicException, UnknownRoleException {
        UserRolesSQLStorage storage = StorageLayer.getUserRolesStorage(main);
        try {
            storage.startTransaction(con -> {
                try {
                    storage.createNewRole_Transaction(con, role);
                } catch (DuplicateRoleException e) {
                    // ignore exception
                }
                if (permissions != null) {
                    for (int i = 0; i < permissions.length; i++) {
                        try {
                            storage.addPermissionToRole_Transaction(con, role, permissions[i]);
                        } catch (DuplicateRolePermissionMappingException e) {
                            // ignore exception
                        } catch (UnknownRoleException e) {
                            throw new StorageTransactionLogicException(e);
                        }
                    }
                }

                return null;
            });
        } catch (StorageTransactionLogicException e) {
            if (e.actualException instanceof UnknownRoleException) {
                throw (UnknownRoleException) e.actualException;
            }
            throw e;
        }
    }

    // remove a role mapped to a user, if the role doesn't exist throw a UNKNOWN_ROLE_EXCEPTION error
//    public static void deleteUserRole(Main main, String userId, String role)
//            throws StorageQueryException, StorageTransactionLogicException {
//
//        UserRolesSQLStorage storage = StorageLayer.getUserRolesStorage(main);
//
//        storage.startTransaction(con -> {
//
//            boolean doesRoleExist = storage.doesRoleExist_Transaction(con, role);
//
//            if (doesRoleExist) {
//                storage.deleteRoleForUser_Transaction(con, userId, role);
//            } else {
//                throw new UnknownRoleException();
//            }
//
//            return null;
//        });
//    }
//
//    // retrieve all roles associated with the user
//    public static String[] getRolesForUser(Main main, String userId) throws StorageQueryException {
//        return StorageLayer.getUserRolesStorage(main).getRolesForUser(userId);
//    }
//
//    // retrieve all users who have the input role, if role does not exist then throw UNKNOWN_ROLE_EXCEPTION
//    public static String[] getUsersForRole(Main main, String role) throws StorageQueryException, UnknownRoleException {
//        UserRolesSQLStorage storage = StorageLayer.getUserRolesStorage(main);
//        boolean doesRoleExist = storage.doesRoleExist(role);
//        if (doesRoleExist) {
//            return storage.getUsersForRole(role);
//        } else {
//            throw new UnknownRoleException();
//        }
//    }
//
//
//    // retrieve all permissions associated with the role
//    public static String[] getPermissionsForRole(Main main, String role)
//            throws StorageQueryException, UnknownRoleException {
//        return StorageLayer.getUserRolesStorage(main).getPermissionsForRole(role);
//    }
//
//    // delete permissions from a role, if the role doesn't exist throw an UNKNOWN_ROLE_EXCEPTION
//    public static void deletePermissionsFromRole(Main main, String role, @Nullable String[] permissions)
//            throws StorageQueryException, StorageTransactionLogicException {
//        UserRolesSQLStorage storage = StorageLayer.getUserRolesStorage(main);
//        storage.startTransaction(con -> {
//            boolean doesRoleExist = storage.doesRoleExist_Transaction(con, role);
//            if (doesRoleExist) {
//                if (permissions == null) {
//                    storage.deleteAllPermissionsForRole_Transaction(con, role);
//                } else {
//                    for (int i = 0; i < permissions.length; i++) {
//                        storage.deletePermissionForRole_Transaction(con, role, permissions[i]);
//                    }
//                }
//            } else {
//                throw new UnknownRoleException();
//            }
//            return null;
//        });
//    }
//
//    // retrieve roles that have the input permission
//    public static String[] getRolesThatHavePermission(Main main, String permission) throws StorageQueryException {
//        return StorageLayer.getUserRolesStorage(main).getRolesThatHavePermission(permission);
//    }
//
//    // delete a role
//    public static int deleteRole(Main main, String role) throws StorageQueryException {
//        return StorageLayer.getUserRolesStorage(main).deleteRole(role);
//    }
//
//    // retrieve all roles that have been created
//    public static String[] getRoles(Main main) throws StorageQueryException {
//        return StorageLayer.getUserRolesStorage(main).getRoles();
//    }
//
//    // delete all roles associated with a user
//    public static int deleteAllRolesForUser(Main main, String userId) throws StorageQueryException {
//        return StorageLayer.getUserRolesStorage(main).deleteAllRolesForUser(userId);
//    }

}