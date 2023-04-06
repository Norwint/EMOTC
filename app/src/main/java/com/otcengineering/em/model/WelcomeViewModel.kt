package com.otcengineering.em.model

import com.otc.alice.api.model.General
import com.otc.alice.api.model.General.TermAcceptance
import com.otc.alice.api.model.Shared
import com.otc.alice.api.model.Shared.OTCResponse
import com.otc.alice.api.model.Vehicle
import com.otc.alice.api.model.Welcome
import com.otc.alice.api.model.Welcome.LoginResponse
import com.otc.alice.api.model.Welcome.PushTokenRegistration
import com.otc.alice.api.model.Welcome.TermsAcceptanceResponse
import com.otcengineering.em.utils.*
import com.otcengineering.otcble.remote.NetworkSDK
import io.reactivex.rxjava3.core.Observable
import java.io.IOException

class WelcomeViewModel {

    private val network: NetworkSDK by lazy { Common.network }

    private var countryId = 0

    var error = ""

    fun getCountry(countryName: String): Observable<Welcome.CountriesResponse> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val resp = Common.network.welcomeApi.getCountries().execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null..."))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = resp.data.unpack(Welcome.CountriesResponse::class.java)
                    if (rsp != null) {
                        for(i in 0 until rsp.countriesList.size){
                            if (rsp.countriesList[i].name == countryName){
                                countryId = rsp.countriesList[i].id
                                Common.sharedPreferences.putString(Constants.Preferences.COUNTRY,rsp.countriesList[i].name)
                            }
                        }
                        observable.onNext(rsp)
                    } else {
                        observable.onError(OtcException(Shared.OTCStatus.NO_DATA))
                    }
                } else {
                    observable.onError(OtcException(resp.status))
                }
            }
        }
    }
    fun loginAsk(phoneNumber: String): Observable<Unit> {
        return Observable.create { observable ->
            val loginRequest = Welcome.LoginV2.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setCountryId(countryId)
                .setMobileIMEI(getIMEI())
                .build()

            executeAPI(observable) {
                val response = network.welcomeApi.loginAsk(loginRequest).execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status != Shared.OTCStatus.SUCCESS) {
                    error = response.status.toString()
                    observable.onError(OtcException(response.status))
                } else {
                    Common.sharedPreferences.putString(Constants.Preferences.PHONE_NUMBER, phoneNumber)
                    observable.onNext(Unit)
                }
            }
        }
    }

    fun loginCheck(otp: String): Observable<Welcome.LoginResponseV2> {
        return Observable.create { observable ->
            val loginRequest = Welcome.LoginOTPCheck.newBuilder()
                .setPhoneNumber(Common.sharedPreferences.getString(Constants.Preferences.PHONE_NUMBER))
                .setCountryId(countryId)
                .setMobileIMEI(getIMEI())
                .setOneTimePassword(otp)
                .build()

            executeAPI(observable) {
                val response = network.welcomeApi.loginCheck(loginRequest).execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status != Shared.OTCStatus.SUCCESS) {
                    observable.onError(OtcException(response.status))
                } else {
                    val loginResponse = response.data.unpack(Welcome.LoginResponseV2::class.java)
                    network.tokenSession.setAuthToken(loginResponse.apiToken)
                    Common.sharedPreferences.putString(Constants.Preferences.TOKEN, loginResponse.apiToken)
                    network.tokenSession.setUserID(loginResponse.userId)
                    observable.onNext(loginResponse)
                }
            }
        }
    }

    fun registerAsk(phoneNumber: String): Observable<Unit> {
        return Observable.create { observable ->
            val registerRequest = Welcome.UserRegistrationV2.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setCountryId(countryId)
                .setMobileIMEI(getIMEI())
                .build()

            executeAPI(observable) {
                val response = network.welcomeApi.registerAsk(registerRequest).execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status != Shared.OTCStatus.SUCCESS) {
                    error = response.status.toString()
                    observable.onError(OtcException(response.status))
                } else {
                    Common.sharedPreferences.putString(Constants.Preferences.PHONE_NUMBER, phoneNumber)
                    // Common.sharedPreferences.putString(Constants.Preferences.IMEI, getIMEI())
                    observable.onNext(Unit)
                }
            }
        }
    }

    fun registerCheck(otp: String): Observable<Welcome.LoginResponseV2> {
        return Observable.create { observable ->

            val loginRequest = Welcome.RegistrationOTPCheck.newBuilder()
                .setPhoneNumber(Common.sharedPreferences.getString(Constants.Preferences.PHONE_NUMBER))
                .setCountryId(countryId)
                .setMobileIMEI(getIMEI())
                .addTerms(TermAcceptance.newBuilder()
                    .setType(General.TermType.DISCLAIMER)
                    .setMobileIdentifier(getIMEI())
                    .setTimestamp(TimeUtils.getTimeForServer())
                    .setVersion("1.0")
                )
                .setOneTimePassword(otp)
                .build()

            executeAPI(observable) {
                val response = network.welcomeApi.registerCheck(loginRequest).execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status != Shared.OTCStatus.SUCCESS) {
                    observable.onError(OtcException(response.status))
                } else {
                    val registerResponse = response.data.unpack(Welcome.LoginResponseV2::class.java)
                    network.tokenSession.setAuthToken(registerResponse.apiToken)
                    network.tokenSession.setUserID(registerResponse.userId)
                    observable.onNext(registerResponse)
                }
            }
        }
    }

    fun getVehicleList(): Observable<List<Vehicle.VehicleData>> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val response = network.vehicleApi.getVehicles().execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status == Shared.OTCStatus.SUCCESS) {
                    val vehList = response.data.unpack(Vehicle.VehicleResponse::class.java)
                    observable.onNext(vehList.vehiclesList)
                } else {
                    observable.onError(OtcException(response.status))
                }
            }
        }
    }

    fun putVehicleLink(vin: String): Observable<Vehicle.VehicleData>{
        return Observable.create { observable ->

            val linkVehicle = Vehicle.VehicleLink.newBuilder()
                .setSerialNumber(vin)
                .build()

            executeAPI(observable) {
                val response = network.vehicleApi.linkVehicle(linkVehicle).execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                    if (response.status == Shared.OTCStatus.SUCCESS) {
                    val vehList = response.data.unpack(Vehicle.VehicleData::class.java)
                    observable.onNext(vehList)
                } else {
                    observable.onError(OtcException(response.status))
                }
            }
        }
    }

    fun refreshLogin() : Observable<LoginResponse> {
        return Observable.create { observable ->

            executeAPI(observable) {
                val response = network.welcomeApi.refreshToken().execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status == Shared.OTCStatus.SUCCESS) {
                    val refresh = response.data.unpack(LoginResponse::class.java)
                    network.tokenSession.setAuthToken(refresh.apiToken)
                    Common.sharedPreferences.putString(Constants.Preferences.TOKEN, refresh.apiToken)
                    network.tokenSession.setUserID(refresh.userId)
                    observable.onNext(refresh)
                } else {
                    observable.onError(OtcException(response.status))
                }
            }
        }
    }

    fun putNotificationToken() : Observable<PushTokenRegistration> {
        return Observable.create { observable ->

            val token = PushTokenRegistration.newBuilder()
                .setToken(Common.sharedPreferences.getString(Constants.Preferences.FIREBASE_TOKEN))
                .setPlatform(PushTokenRegistration.Platform.GCM)
                .setProject(PushTokenRegistration.Project.OTC)
                .build()

            executeAPI(observable) {
                val response = network.welcomeApi.sendPushToken(token).execute().body()

                if (response == null) {
                    observable.onError(IOException("Cannot execute API"))
                    return@executeAPI
                }

                if (response.status == Shared.OTCStatus.SUCCESS) {
                    val refresh = response.data.unpack(PushTokenRegistration::class.java)
                    observable.onNext(refresh)
                } else {
                    observable.onError(OtcException(response.status))
                }
            }
        }
    }
}