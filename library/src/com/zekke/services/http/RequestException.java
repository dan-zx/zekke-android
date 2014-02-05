/*
 * Copyright 2013 ZeKKe Project
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
package com.zekke.services.http;

public class RequestException extends RuntimeException {

    private static final long serialVersionUID = 3424062746528636260L;

    private final String errorType;
    private Integer messageResId;
    private String message;

    public RequestException(String message, String errorType) {
        super(message);
        this.message = message;
        this.errorType = errorType;
    }

    public RequestException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.errorType = throwable.getClass().getSimpleName();
    }

    public RequestException(int messageResId, String errorType) {
        this.messageResId = messageResId;
        this.errorType = errorType;
    }

    public RequestException(int messageResId, Throwable throwable) {
        super(throwable);
        this.messageResId = messageResId;
        this.errorType = throwable.getClass().getSimpleName();
    }

    public String getErrorType() {
        return errorType;
    }

    public Integer getMessageResId() {
        return messageResId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static class Builder {

        private String errorType;
        private String message;
        private Integer messageResId;
        private Throwable throwable;

        public Builder setErrorType(String errorType) {
            this.errorType = errorType;
            return this;
        }

        public Builder setErrorType(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessageResource(int messageResId) {
            this.messageResId = messageResId;
            return this;
        }

        public RequestException build() {
            if (throwable != null && messageResId != null) {
                return new RequestException(messageResId, throwable);
            } else if (throwable == null && messageResId != null) {
                return new RequestException(messageResId, errorType);
            } else if (throwable != null && messageResId == null) {
                return new RequestException(message, throwable);
            } else {
                return new RequestException(message, errorType);
            }
        }
    }
}