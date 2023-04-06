package com.otcengineering.em.model

import com.otc.alice.api.model.DashboardAndStatus
import com.otc.alice.api.model.General.UserProfile
import com.otc.alice.api.model.ProfileAndSettings
import com.otc.alice.api.model.ProfileAndSettings.UserProfilePart1
import com.otc.alice.api.model.ProfileAndSettings.UserProfilePart2
import com.otc.alice.api.model.ProfileAndSettings.UserProfileResponse
import com.otc.alice.api.model.Shared
import com.otc.alice.api.model.Vehicle
import com.otc.alice.api.model.Welcome
import com.otcengineering.em.data.DashboardData
import com.otcengineering.em.data.DashboardDataJoin
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.executeAPI
import com.otcengineering.em.utils.getIMEI
import com.otcengineering.otcble.remote.NetworkSDK
import io.reactivex.rxjava3.core.Observable
import java.io.IOException

class ProfileViewModel {

    private var countryId = 0

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

    fun getProfile(): Observable<UserProfileResponse> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val resp = Common.network.profileApi.getProfile().execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null"))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = resp.data.unpack(UserProfileResponse::class.java)
                    observable.onNext(rsp)
                } else {
                    observable.onError(OtcException(resp.status))
                }
            }
        }
    }

    fun putProfile(username: String, name: String, surname: String, birthday: String, email: String): Observable<UserProfileResponse> {
        return Observable.create { observable ->

            val profileRequest = UserProfile.newBuilder()
                .setUsername(username)
                .setName(name)
                .setCountryId(countryId)
                .setSurname(surname)
                .setBirthdayDate(birthday)
                .setEmail(email)
                .build()

            executeAPI(observable) {
                val resp = Common.network.profileApi.putProfile(profileRequest).execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null"))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = resp.data.unpack(UserProfileResponse::class.java)
                    observable.onNext(rsp)
                } else {
                    observable.onError(OtcException(resp.status))
                }
            }
        }
    }
}