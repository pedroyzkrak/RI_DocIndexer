/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package support;

/**
 *
 * @author Pedro
 */
public class Query {
    private final int id;
    private String querystr;

    public Query(int id, String str) {
        this.id = id;
        this.querystr = str;
    }

    public String getStr() {
        return querystr;
    }

    public void setStr(String str) {
        this.querystr = str;
    }
    
    
}
