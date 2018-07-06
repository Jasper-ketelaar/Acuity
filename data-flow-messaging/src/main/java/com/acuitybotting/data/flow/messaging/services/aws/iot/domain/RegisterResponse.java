package com.acuitybotting.data.flow.messaging.services.aws.iot.domain;

/**
 * Created by Zachary Herridge on 7/6/2018.
 */
public class RegisterResponse {

    private ResponseMetadata ResponseMetadata;
    private Credentials Credentials;
    private AssumedRoleUser AssumedRoleUser;

    public RegisterResponse.ResponseMetadata getResponseMetadata() {
        return ResponseMetadata;
    }

    public RegisterResponse.Credentials getCredentials() {
        return Credentials;
    }

    public RegisterResponse.AssumedRoleUser getAssumedRoleUser() {
        return AssumedRoleUser;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegisterResponse{");
        sb.append("ResponseMetadata=").append(ResponseMetadata);
        sb.append(", Credentials=").append(Credentials);
        sb.append(", AssumedRoleUser=").append(AssumedRoleUser);
        sb.append('}');
        return sb.toString();
    }

    public static class ResponseMetadata {
        private String RequestId;

        public String getRequestId() {
            return RequestId;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ResponseMetadata{");
            sb.append("RequestId='").append(RequestId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Credentials {
        private String AccessKeyId;
        private String SecretAccessKey;
        private String SessionToken;
        private String Expiration;

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public String getSecretAccessKey() {
            return SecretAccessKey;
        }

        public String getSessionToken() {
            return SessionToken;
        }

        public String getExpiration() {
            return Expiration;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Credentials{");
            sb.append("AccessKeyId='").append(AccessKeyId).append('\'');
            sb.append(", SecretAccessKey='").append(SecretAccessKey).append('\'');
            sb.append(", SessionToken='").append(SessionToken).append('\'');
            sb.append(", Expiration='").append(Expiration).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static class AssumedRoleUser {
        private String AssumedRoleId;
        private String Arn;

        public String getAssumedRoleId() {
            return AssumedRoleId;
        }

        public String getArn() {
            return Arn;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("AssumedRoleUser{");
            sb.append("AssumedRoleId='").append(AssumedRoleId).append('\'');
            sb.append(", Arn='").append(Arn).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
