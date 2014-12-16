/* 
**
** Copyright 2014, Jules White
**
** 
*/
package com.potlatchClient;

public interface TaskCallback<T> {

    public void success(T result);

    public void error(Exception e);

}
