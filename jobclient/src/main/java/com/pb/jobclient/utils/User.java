package com.pb.jobclient.utils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The purpose of this class is to store the role of the user logged into the Desktop Client. 
 * The behavior of the Desktop Client is a function of the user role. A Service User can download accumulated print streams
 * while other user roles cannot. A Service User can also upload and download files like any other user.
 * 
 * @author jo007ma
 *
 */
public class User {

    public enum UserRole { ADMIN("ADMINROLE"), OPERATOR("MAILROOMOPERATORROLE"), CHIEF_OPERATOR("CHIEFOPERATORROLE"), SERVICE_USER("SERVICEUSERROLE"), USER("USERROLE");
        
        private String name;
        
        UserRole(String name) {
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
    }
    
    
    private static boolean isServiceUser = false;
    
    private static List<String> roles;
    
    /** Don't allow class to be instanced */
    private User() {
        
    }
    
    public static void setRoles(List<String> roles) {
        
        if(null == roles) 
            return;
        
        User.roles = roles;
        
        List<String> userServiceRole = roles.stream()
                .filter(r -> r.equalsIgnoreCase(UserRole.SERVICE_USER.toString()))
                .collect(Collectors.toList());
        
        isServiceUser = null != userServiceRole && !userServiceRole.isEmpty();
    }
    
    
    public static List<String> getRoles() {
        return roles;
    }
    
    
    public static boolean isServiceUser() {
        return isServiceUser;
    }

}
