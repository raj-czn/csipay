package com.csipay.softpos.api;

public interface HealthCheckListener {

    void onHealthCheckComplete(boolean isHealthy, String message);

    void onHealthCheckError(SdkError error);
}