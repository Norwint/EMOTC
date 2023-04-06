package com.otcengineering.em.model

import android.util.Log
import com.google.protobuf.ByteString
import com.otc.alice.api.model.*
import com.otc.alice.api.model.Service.CommunityResponse
import com.otc.alice.api.model.Service.TicketType
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.executeAPI
import io.reactivex.rxjava3.core.Observable
import java.io.IOException

class ServicesViewModel {

    private var fileId = 0L

    private var chunkSize = 0

    fun getURL(): Observable<CommunityResponse> {
        return Observable.create { observable ->
            executeAPI(observable) {
                val resp = Common.network.serviceApi.getURLCommunity().execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null"))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    val rsp = resp.data.unpack(CommunityResponse::class.java)
                    observable.onNext(rsp)
                } else {
                    observable.onError(OtcException(resp.status))
                }
            }
        }
    }

    fun postTicket(message: String, ticketType: TicketType, array: ArrayList<Long>) : Observable<Unit>{
        return Observable.create { observable ->

            val ticket = Service.Ticket.newBuilder()
                .setMessage(message)
                .setType(ticketType)
                .addAllFilesId(array)
                .build()

            executeAPI(observable) {
                val resp = Common.network.serviceApi.sendTicket(Common.sharedPreferences.getString(
                    Constants.Preferences.VEHICLE_ID).toInt(), ticket).execute().body()
                if (resp == null) {
                    observable.onError(IOException("Response is null"))
                    return@executeAPI
                }

                if (resp.status == Shared.OTCStatus.SUCCESS) {
                    Log.d("ticket-send", "ok")
                } else {
                    observable.onError(OtcException(resp.status))
                    Log.d("ticket-send", resp.status.toString())
                }
            }
        }
    }

    fun uploadBulk(data: ByteArray, fileName: String) : Observable<FileProto.UploadFileBulkResponse>{
        return Observable.create { observable ->

            var nTotalParts = data.size / 1048576

            if (data.size % 1048576 > 1) {
                    nTotalParts += 1
             }

            sendFilePart(0, fileName, data, 1, nTotalParts)
        }
    }

    private fun sendFilePart(fileID: Long, fileName: String, data: ByteArray, nChunk: Int, nTotalChunks: Int) : Observable<FileProto.UploadFileBulkResponse> {

        return Observable.create { observable ->

             val uploadChunkSize = 1048576

             val totalSize = data.size

             if ((nChunk * uploadChunkSize) > totalSize) {
                 chunkSize = totalSize - ((nChunk - 1) * uploadChunkSize)
             } else {
                 chunkSize = uploadChunkSize
             }

             var chunk = ByteArray(chunkSize)

             chunk = data.copyInto(chunk, 0, (nChunk - 1) * uploadChunkSize, ((nChunk - 1) * uploadChunkSize)+chunkSize)

             val file = FileProto.UploadFileBulk.newBuilder()
                 .setFileName(fileName)
                 .setFileNameBytes(ByteString.copyFrom(chunk))
                 .setTotalParts(nTotalChunks)
                 .setCurrentPart(nChunk)
                 .setFileId(fileID)
                 .build()

             executeAPI(observable) {
                 val resp = Common.network.uploadApi.bulkUpload(file).execute().body()
                 if (resp == null) {
                     observable.onError(IOException("Response is null"))
                     return@executeAPI
                 }

                 if (resp.status == Shared.OTCStatus.SUCCESS) {
                     if (nChunk == nTotalChunks) {
                         val rsp = resp.data.unpack(FileProto.UploadFileBulkResponse::class.java)
                         observable.onNext(rsp)
                     } else if (resp.status == Shared.OTCStatus.INVALID_AUTHORIZATION) {
                        "LOGOUT"
                     } else {
                         val rsp = resp.data.unpack(FileProto.UploadFileBulkResponse::class.java)
                         sendFilePart(rsp.fileId, fileName, data, nChunk + 1, nTotalChunks).subscribe({
                             observable.onNext(it)
                         }, {
                             observable.onError(it)

                         })
                     }
                     Log.d("ticket-send", "ok")
                 } else {
                     observable.onError(OtcException(resp.status))
                     Log.d("ticket-send", resp.status.toString())
                 }
             }

        }

    }

}