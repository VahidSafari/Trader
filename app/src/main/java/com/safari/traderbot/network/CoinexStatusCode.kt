package com.safari.traderbot.network

import com.google.gson.annotations.SerializedName

enum class CoinexStatusCode(statusCode: Int) {
    @SerializedName("0")
    SUCCEEDED(0),
    @SerializedName("1")
    ERROR(1),
    @SerializedName("2")
    INVALID_ARGUMENT(2),
    @SerializedName("3")
    INTERNAL_ERROR(3),
    @SerializedName("23")
    IP_PROHIBITED(23),
    @SerializedName("24")
    ACCESS_ID_DOES_NOT_EXIST(24),
    @SerializedName("25")
    SIGNATURE_ERROR(25),
    @SerializedName("34")
    ACCESS_ID_EXPIRED(34),
    @SerializedName("35")
    SERVICE_UNAVAILABLE(35),
    @SerializedName("36")
    SERVICE_TIMEOUT(36),
    @SerializedName("40")
    MAIN_ACCOUNT_AND_SUB_ACCOUNT_DO_NOT_MATCH(40),
    @SerializedName("49")
    THE_TRANSFER_TO_THE_SUB_ACCOUNT_WAS_REJECTED(49),
    @SerializedName("107")
    INSUFFICIENT_BALANCE(107),
    @SerializedName("158")
    NO_PERMISSION_TO_USE_THIS_API(158),
    @SerializedName("213")
    PLEASE_DO_NOT_TRY_TOO_FREQUENTLY(213),
    @SerializedName("227")
    THE_TIMESTAMP_IS_WRONG(227),
    @SerializedName("600")
    ORDER_NUMBER_DOES_NOT_EXIST(600),
    @SerializedName("601")
    OTHER_USERS_ORDERS(601),
    @SerializedName("602")
    BELOW_THE_MINIMUM_LIMIT_FOR_BUYING_OR_SELLING(602),
    @SerializedName("606")
    THE_ORDER_PRICE_DEVIATES_TOO_MUCH_FROM_THE_LATEST_TRANSACTION_PRICE(606),
    @SerializedName("651")
    MERGE_DEPTH_ERROR(651);
}