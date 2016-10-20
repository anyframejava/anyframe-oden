/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.admin.domain;

import java.io.Serializable;

/**
 * Domain class for user info.
 * 
 * @author Junghwan Hong
 * @author Sujeong Lee
 */
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String userId;
    private String userName;
    private String password;
    private String enabled;
    private String createDate;
    private String modifyDate;
    
    private String role;
	private String job;
	private String hidden;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnabled() {
        return this.enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return this.modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getJob() {
		return job;
	}
	
	public void setJob(String job) {
		this.job = job;
	}
	
	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public String getHidden() {
		return hidden;
	}
	
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        User pojo = (User) o;

        if ((userName != null) ? (!userName.equals(pojo.userName))
                                   : (pojo.userName != null)) {
            return false;
        }

        if ((password != null) ? (!password.equals(pojo.password))
                                   : (pojo.password != null)) {
            return false;
        }

        if ((enabled != null) ? (!enabled.equals(pojo.enabled))
                                  : (pojo.enabled != null)) {
            return false;
        }

        if ((createDate != null) ? (!createDate.equals(pojo.createDate))
                                     : (pojo.createDate != null)) {
            return false;
        }

        if ((modifyDate != null) ? (!modifyDate.equals(pojo.modifyDate))
                                     : (pojo.modifyDate != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = 0;
        result = ((userName != null) ? userName.hashCode() : 0);
        result = (31 * result) + ((password != null) ? password.hashCode() : 0);
        result = (31 * result) + ((enabled != null) ? enabled.hashCode() : 0);
        result = (31 * result) +
            ((createDate != null) ? createDate.hashCode() : 0);
        result = (31 * result) +
            ((modifyDate != null) ? modifyDate.hashCode() : 0);

        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());

        sb.append(" [");
        sb.append("userId").append("='").append(getUserId()).append("', ");
        sb.append("userName").append("='").append(getUserName()).append("', ");
        sb.append("password").append("='").append(getPassword()).append("', ");
        sb.append("enabled").append("='").append(getEnabled()).append("', ");
        sb.append("createDate").append("='").append(getCreateDate())
          .append("', ");
        sb.append("modifyDate").append("='").append(getModifyDate()).append("'");
        sb.append("]");

        return sb.toString();
    }
}