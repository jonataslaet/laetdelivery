package br.com.microservices.orchestrated.orchestratorservice.handlers;

import static br.com.microservices.orchestrated.orchestratorservice.enums.EventSourceEnum.ORCHESTRATOR;
import static br.com.microservices.orchestrated.orchestratorservice.enums.EventSourceEnum.PRODUCT_VALIDATION_SERVICE;
import static br.com.microservices.orchestrated.orchestratorservice.enums.EventSourceEnum.PAYMENT_SERVICE;
import static br.com.microservices.orchestrated.orchestratorservice.enums.EventSourceEnum.INVENTORY_SERVICE;
import static br.com.microservices.orchestrated.orchestratorservice.enums.SagaStatusEnum.SUCCESS;
import static br.com.microservices.orchestrated.orchestratorservice.enums.SagaStatusEnum.FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.enums.SagaStatusEnum.ROLLBACK_PENDING;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.PRODUCT_VALIDATION_SUCCESS;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.FINISH_FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.PRODUCT_VALIDATION_FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.PAYMENT_SUCCESS;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.PAYMENT_FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.INVENTORY_SUCCESS;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.INVENTORY_FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.FINISH_SUCCESS;

public final class SagaHandler {

    private SagaHandler() {

    }

    public static final Object[][] SAGA_HANDLER = {
        {ORCHESTRATOR, SUCCESS, PRODUCT_VALIDATION_SUCCESS},
        {ORCHESTRATOR, FAIL, FINISH_FAIL},

        {PRODUCT_VALIDATION_SERVICE, ROLLBACK_PENDING, PRODUCT_VALIDATION_FAIL},
        {PRODUCT_VALIDATION_SERVICE, FAIL, FINISH_FAIL},
        {PRODUCT_VALIDATION_SERVICE, SUCCESS, PAYMENT_SUCCESS},

        {PAYMENT_SERVICE, ROLLBACK_PENDING, PAYMENT_FAIL},
        {PAYMENT_SERVICE, FAIL, PRODUCT_VALIDATION_FAIL},
        {PAYMENT_SERVICE, SUCCESS, INVENTORY_SUCCESS},

        {INVENTORY_SERVICE, ROLLBACK_PENDING, INVENTORY_FAIL},
        {INVENTORY_SERVICE, FAIL, PAYMENT_FAIL},
        {INVENTORY_SERVICE, SUCCESS, FINISH_SUCCESS},
    };
}
