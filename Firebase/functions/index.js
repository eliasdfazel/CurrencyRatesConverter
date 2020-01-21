'use strict';

const functions = require('firebase-functions');
const XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;

var serviceAccount = require('./serviceaccountkey.json');
const admin = require('firebase-admin');
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const FieldValue = require('firebase-admin').firestore.FieldValue;
const runtimeOptions = {
    timeoutSeconds: 20
}

//Map of Countries Currency Code with Currency Name
const countryMap = {
    AUD: "Australian Dollar",
    BGN: "Bulgarian Lev",
    BRL: "Brazilian Real",
    CAD: "Canadian Dollar",
    CHF: "Swiss Franc",
    CNY: "Chinese Yuan",
    CZK: "Czech Koruna",
    DKK: "Danish Krone",
    EUR: "European Euro",
    GBP: "British Pound",
    HKD: "Hong Kong Dollar",
    HRK: "Croatian Kuna",
    HUF: "Hungarian Forint",
    IDR: "Indonesian Rupiah",
    ILS: "Israeli Shekel",
    INR: "Indian Rupee",
    ISK: "Icelandic Króna",
    JPY: "Japanese Yen",
    KRW: "South Korean Won",
    MXN: "Mexican Peso",
    MYR: "Malaysian Ringgit",
    NOK: "Norwegian Krone",
    NZD: "New Zealand Dollar",
    PHP: "Philippine Piso",
    PLN: "Polish Złoty",
    RON: "Romanian Leu",
    RUB: "Russian Ruble",
    SEK: "Swedish Krona",
    SGD: "Singapore Dollar",
    THB: "Thai Baht",
    TRY: "Turkish Lira",
    USD: "United States Dollar",
    ZAR: "South African Rand"
}   

//Downloading and Extracting JsonObject of Currency Rates
//and Create Users specific database directory with initial order
exports.firstSetupCurrencyRates = functions.runWith(runtimeOptions).https.onCall((data, context) => {
    const userId = data.userId;

    const baseLink = "https://revolut.duckdns.org/latest?base=";
    const currencyCode = data.apiLink;
    const apiLink = baseLink + currencyCode;
    console.log("Geeks Empire " + apiLink);

    const firestore = admin.firestore();
    try {
        const settings = { timestampsInSnapshots: true };
        firestore.settings(settings);
    } catch (exceptions) { }

    var xmlHttpRequest = new XMLHttpRequest();
    xmlHttpRequest.open('GET', apiLink, true);
    xmlHttpRequest.setRequestHeader('accept', 'application/json');
    xmlHttpRequest.onreadystatechange = function () {
        if (this.readyState == 4) {

        } else {

        }
    };
    xmlHttpRequest.onprogress = function () {

    };
    xmlHttpRequest.onload = function () {
        /*Extract JSON & Update Items*/
        var baseCurrency = JSON.parse(xmlHttpRequest.responseText)['base'];
        var updateDate = JSON.parse(xmlHttpRequest.responseText)['date'];
        var CurrencyRates = JSON.parse(xmlHttpRequest.responseText)['rates'];

        var batch = firestore.batch();
        for (var jsonObjectKey in CurrencyRates) {
            var indexOfValue = Object.keys(CurrencyRates).indexOf(jsonObjectKey);
            console.log('GeeksEmpire ' + indexOfValue + ". " + jsonObjectKey + " - " + CurrencyRates[jsonObjectKey]);

            try {
                var currencyReference = firestore.collection("Currency/" + userId + "/Rates").doc(jsonObjectKey);
                batch.set/*set*/(currencyReference, {
                    CountryName: countryMap[jsonObjectKey],
                    Name: jsonObjectKey,
                    Rate: parseFloat(CurrencyRates[jsonObjectKey].toFixed(3)),
                    LastUpdate: FieldValue.serverTimestamp(),
                    IndexOfValue: indexOfValue,
                    LastCurrencyClickTime: 0
                });
            }
            catch (error) {
                console.log(error)
            }
        }

        batch.set/*set*/(firestore.collection("Currency/" + userId + "/Rates").doc(currencyCode), {
            CountryName: countryMap[currencyCode],
            Name: currencyCode,
            Rate: 1.00,
            LastUpdate: FieldValue.serverTimestamp(),
            IndexOfValue: indexOfValue,
            LastCurrencyClickTime: 0
        });

        batch.commit().then(function () {
            console.log("Document 👍");
        });
    };
    xmlHttpRequest.send();
});

//Updating Currency Rates
exports.updateCurrencyRates = functions.runWith(runtimeOptions).https.onCall((data, context) => {
    const userId = data.userId;

    const baseLink = "https://revolut.duckdns.org/latest?base=";
    const currencyCode = data.apiLink;
    const apiLink = baseLink + currencyCode;
    console.log("Geeks Empire " + apiLink);

    const firestore = admin.firestore();
    try {
        const settings = { timestampsInSnapshots: true };
        firestore.settings(settings);
    } catch (exceptions) { }

    var xmlHttpRequest = new XMLHttpRequest();
    xmlHttpRequest.open('GET', apiLink, true);
    xmlHttpRequest.setRequestHeader('accept', 'application/json');
    xmlHttpRequest.onreadystatechange = function () {
        if (this.readyState == 4) {

        } else {

        }
    };
    xmlHttpRequest.onprogress = function () {

    };
    xmlHttpRequest.onload = function () {
        /*Extract JSON & Update Items*/
        var baseCurrency = JSON.parse(xmlHttpRequest.responseText)['base'];
        var updateDate = JSON.parse(xmlHttpRequest.responseText)['date'];
        var CurrencyRates = JSON.parse(xmlHttpRequest.responseText)['rates'];

        var batch = firestore.batch();
        for (var jsonObjectKey in CurrencyRates) {
            var indexOfValue = Object.keys(CurrencyRates).indexOf(jsonObjectKey);
            console.log('GeeksEmpire ' + indexOfValue + ". " + jsonObjectKey + " - " + CurrencyRates[jsonObjectKey]);

            try {
                var currencyReference = firestore.collection("Currency/" + userId + "/Rates").doc(jsonObjectKey);
                batch.update/*set*/(currencyReference, {
                    CountryName: countryMap[jsonObjectKey],
                    Name: jsonObjectKey,
                    Rate: parseFloat(CurrencyRates[jsonObjectKey].toFixed(3)),
                    LastUpdate: FieldValue.serverTimestamp(),
                    IndexOfValue: indexOfValue,
                });
            }
            catch (error) {
                console.log(error)
            }
        }

        batch.update/*set*/(firestore.collection("Currency/" + userId + "/Rates").doc(currencyCode), {
            CountryName: countryMap[currencyCode],
            Name: currencyCode,
            Rate: 1.00,
            LastUpdate: FieldValue.serverTimestamp(),
            IndexOfValue: indexOfValue,
        });

        batch.commit().then(function () {
            console.log("Document 👍");
        });
    };
    xmlHttpRequest.send();
});