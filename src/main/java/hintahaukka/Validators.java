package hintahaukka;

import spark.Request;

/**
 * This class contains all the static methods used to validate 
 * input given to the server.
 */
public class Validators {
    
    static boolean eanOk(Request req){
        if(req.queryParams().size() != 1) {
            return false;
        }
        if(!req.queryParams().contains("ean")) {
            return false;
        }
        if(req.queryParamsValues("ean").length != 1) {
            return false;
        }
        if(req.queryParams("ean").length() < 8) {
            return false;
        }
        return true;
    }
    
    static boolean eanCentsStoreIdIdOk(Request req){
        if(req.queryParams().size() != 4 && req.queryParams().size() != 5) {
            return false;
        }
        
        if(!req.queryParams().contains("ean") || !req.queryParams().contains("cents") || !req.queryParams().contains("storeId") || !req.queryParams().contains("id")) {
            return false;
        }
        if(req.queryParams().size() == 5 && !req.queryParams().contains("productName")){
            return false;
        }
        
        if(req.queryParamsValues("ean").length != 1 || req.queryParamsValues("cents").length != 1 || req.queryParamsValues("storeId").length != 1 || req.queryParamsValues("id").length != 1) {
            return false;
        }
        if(req.queryParams().size() == 5 && req.queryParamsValues("productName").length != 1){
            return false;
        }
        
        if(req.queryParams("ean").length() < 8 || req.queryParams("storeId").length() < 1 || req.queryParams("id").length() < 33) {
            return false;
        }
        if(req.queryParams().size() == 5 && req.queryParams("productName").length() > 150){
            return false;
        }
        
        // Cents value check.
        int cents = 0;
        try{
            cents = Integer.parseInt(req.queryParams("cents"));
        }catch(Exception e){
            return false;
        }
        if(cents < 0) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }
    
    static boolean IdNicknameOk(Request req){
        if(req.queryParams().size() != 2) {
            return false;
        }
        if(!req.queryParams().contains("id") || !req.queryParams().contains("nickname")) {
            return false;
        }
        if(req.queryParamsValues("id").length != 1 || req.queryParamsValues("nickname").length != 1) {
            return false;
        }
        
        if(req.queryParams("id").length() < 33 || req.queryParams("nickname").length() < 2 || req.queryParams("nickname").length() > 20) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }
    
    static boolean eanIdProductNameOk(Request req){
        if(req.queryParams().size() != 3) {
            return false;
        }
        
        if(!req.queryParams().contains("ean") || !req.queryParams().contains("id") || !req.queryParams().contains("productName")) {
            return false;
        }
        if(req.queryParamsValues("ean").length != 1 || req.queryParamsValues("id").length != 1 || req.queryParamsValues("productName").length != 1) {
            return false;
        }
        if(req.queryParams("ean").length() < 8 || req.queryParams("id").length() < 33 || req.queryParams("productName").length() > 150 || req.queryParams("productName").length() < 2) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }
    
    static boolean IdEansOk(Request req){
        if(req.queryParams().size() < 2) {
            return false;
        }
        
        if(!req.queryParams().contains("id")) {
            return false;
        }
        if(req.queryParamsValues("id").length != 1) {
            return false;
        }
        
        if(req.queryParams("id").length() < 33) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        int i = 1;
        while(i < req.queryParams().size()) {
            if(!req.queryParams().contains("ean" + i)) {
                return false;
            }
            if(req.queryParamsValues("ean" + i).length != 1) {
                return false;
            }
            if(req.queryParams("ean" + i).length() < 8) {
                return false;
            }            
            
            ++i;
        }
        
        return true;
    }
    
    static boolean IdEanOk(Request req){
        if(req.queryParams().size() != 2) {
            return false;
        }
        
        if(!req.queryParams().contains("id") || !req.queryParams().contains("ean")) {
            return false;
        }
        if(req.queryParamsValues("id").length != 1 || req.queryParamsValues("ean").length != 1) {
            return false;
        }
        
        if(req.queryParams("id").length() < 33 || req.queryParams("ean").length() < 8) {
            return false;
        }
        
        // Id value check of the tokenAndId.
        int id = 0;
        try{ 
            id = Integer.parseInt(req.queryParams("id").substring(32));
        }catch(Exception e){
            return false;
        }
        if(id < 1) {
            return false;
        }
        
        return true;
    }

}
