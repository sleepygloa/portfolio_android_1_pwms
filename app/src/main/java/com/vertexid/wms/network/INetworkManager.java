package com.vertexid.wms.network;

import com.vertexid.wms.info.CarryInInfo;
import com.vertexid.wms.info.CarryNumberInfo;
import com.vertexid.wms.info.CarryOutInfo;
import com.vertexid.wms.info.InquiryInfo;
import com.vertexid.wms.info.InvenInquiryInfo;
import com.vertexid.wms.info.InvenMoveInfo;
import com.vertexid.wms.info.InvenPltInfo;
import com.vertexid.wms.info.InventoryStateChangeInfo;
import com.vertexid.wms.info.SerialScanInfo;
import com.vertexid.wms.info.TakeOutInfo;
import com.vertexid.wms.info.TakeOutLoadingInfo;
import com.vertexid.wms.info.WareHouseInfo;
import com.vertexid.wms.info.WareHouseNumberInfo;
import com.vertexid.wms.scrn.takeout.info.WaveNumberInfo;

import org.json.JSONArray;

/**
 * 서버와의 통신을 위한 외부 공개 API
 */
public interface INetworkManager {
    /**
     * 로그인 요청을 한다.
     * @param id 로그인 요청을 하는 사용자 ID
     * @param password 로그인 요청을 하는 사용자의 비밀번호
     * @return VErrors 참조
     */
    public int reqLogIn(String id, String password);

    /**
     * 로그아웃 요청을 한다.
     * @param id 로그 아웃 요청을 하는 사용자 ID
     * @return VErrors 참조
     */
    public int reqLogOut(String id);

    /**
     * APK 다운로드 요청을 한다.
     * @param path 다운로드 받은 파일을 저장할 위치
     * @return VErrors 참조
     */
    public int reqApkDownLoad(String path);

    /**
     * 입하검수 목록을 요청한다.
     * @param info 입하검수 목록을 요청할 정보 클래스
     * @return VErrors 참조
     */
    public int reqArrivalList(WareHouseInfo info);

    /**
     * 입고번호 목록을 요청한다.
     * @param info 입고번호 목록 요청정보
     * @return VErrors 참조
     */
    public int reqWareHouseNumberList(WareHouseNumberInfo info);

    /**
     * 입하검수 요청
     * @param info 입하검수 요청 정보
     * @return VErrors 참조
     */
    public int reqArrivalCheck(WareHouseInfo info);

    /**
     * 입하검수 확정 요청
     * @param info 입하검수 확정 요청 정보
     * @param json_array 입하검수 확정 요청 목록
     * @return VErrors 참조
     */
    public int reqArrivalConfirm(WareHouseInfo info, JSONArray json_array);

    /**
     * 입고 적치 내용 요청
     * @param info 입고적치 요청 정보
     * @return VErrors 참조
     */
    public int reqWarePileUpSearch(WareHouseInfo info);

    /**
     * 입고 적치 제품조회 요청
     * @param info 입고 적치 제품조회 정보
     * @return VErrors 참조
     */
    public int reqWarePileUpGoodsSearch(WareHouseInfo info);

    /**
     * 입고 적치 확정 요청
     * @param info 입고 적치 확정 정보
     * @return VErrors 참조
     */
    public int reqWarePileUpConfirm(WareHouseInfo info);

    /**
     * 입고 시리얼 스캔 목록을 요청한다.
     * @param info 시리얼 스캔 목록을 요청할 정보
     * @return VErrors 참조
     */
    public int reqWareSerialScanList(SerialScanInfo info);

    /**
     * 입고 시리얼 확정 요청한다.
     * @param info 입고 시리얼 확정을 요청할 정보
     * @return VErrors 참조
     */
    public int reqWareSerialScanConfirm(SerialScanInfo info);

    /**
     * 출고번호 목록을 요청을 한다.
     * @param info 출고번호 목록 정보
     * @return VErrors 참조
     */
    public int reqTakeOutNumberList(TakeOutInfo info);

    /**
     * 피킹리스트 목록을 요청을 한다.
     * @param info 피킹리스트 정보
     * @return VErrors 참조
     */
    public int reqTakeOutPickingList(TakeOutInfo info);

    /**
     * 웨이브 목록을 요청을 한다.
     * @param info 웨이브 목록 정보
     * @return VErrors 참조
     */
    public int reqWaveNumberList(WaveNumberInfo info);

    /**
     * 출고시리얼스캔 리스트를 조회을 요청을 한다.
     * @param info 출고시리얼스캔 정보
     * @return VErrors 참조
     */
    public int reqTakeOutSerialScanlList(TakeOutInfo info);

    /**
     * 출고시리얼스캔 확정을 요청을 한다.
     * @param info 출고시리얼스캔 정보
     * @return VErrors 참조
     */
    public int reqTakeOutSerialScanConfirm(SerialScanInfo info);

    /**
     * 피킹(총량) 리스트를 요청을 한다.
     * @param info 피킹(총량) 리스트 정보
     * @return VErrors 참조
     */
    public int reqTakeOutPickingTotalList(TakeOutInfo info);

    /**
     * 피킹(총량) 확정을 요청한다.
     * @param info 피킹(총량) 확정 정보
     * @return VErrors 참조
     */
    public int reqTakeOutPickingTotalConfirm(TakeOutInfo info);

    /**
     * 분배리스트를 요청을 한다.
     * @param info 분배리스트 정보
     * @return VErrors 참조
     */
    public int reqTakeOutDivideList(TakeOutInfo info);

    /**
     * 분배 상세 목록을 요청한다.
     * @param info 분배 상세 목록을 요청 정보
     * @return VErrors 참조
     */
    public int reqTakeOutDivideDetailList(TakeOutInfo info);

    /**
     * 피킹리스트 확정을 요청을 한다.
     * @param info 피킹리스트 정보
     * @return VErrors 참조
     */
    public int reqTakeOutPickingListConfirm(TakeOutInfo info);

    /**
     * 출하상차리스트 조회을 요청을 한다.
     * @param info 출하상차리스트 정보
     * @return VErrors 참조
     */
    public int reqTakeOutLoadingList(TakeOutInfo info);

    /**
     * 출하상차리스트 상세 목록조회을 요청을 한다.
     * @param info 출하상차리스트 상세 정보
     * @return VErrors 참조
     */
    public int reqTakeOutLoadingDetailList(TakeOutLoadingInfo info);

    /**
     * 출하상차리스트 파렛트 목록조회을 요청을 한다.
     * @param info 출하상차리스트 상세 정보
     * @return VErrors 참조
     */
    public int reqTakeOutLoadingPltList(TakeOutLoadingInfo info);

    /**
     * 상차요청을 한다.
     * @param info 상차요청 정보
     * @return VErrors 참조
     */
    public int reqTakeOutCarLoading(TakeOutLoadingInfo info, JSONArray array);

    /**
     * 반입검수 목록을 요청한다.
     * @param info 반입검수 목록을 요청정보
     * @return VErrors 참조
     */
    public int reqCarryInList(CarryInInfo info);

    /**
     * 반입검수를 요청한다.
     * @param info 반입검수 요청정보
     * @return VErrors 참조
     */
    public int reqCarryInCheck(CarryInInfo info);

    /**
     * 반입검수 확정을 요청한다.
     * @param info 반입검수 요청정보
     * @param array 반입검수 확정요청 목록
     * @return VErrors 참조
     */
    public int reqCarryInConfirm(CarryInInfo info, JSONArray array);

    /**
     * 반입번호 목록을 요청한다.
     * @param info 반입번호 목록 요청 정보
     * @return VErrors 참조
     */
    public int reqCarryInNumberList(CarryNumberInfo info);

    /**
     * 반입 적치 제품 상세 내용을 요청한다.
     * @param info 반입 적치 제품 상세 내용 요청 정보
     * @return VErrors 참조
     */
    public int reqCarryInPileUpSearch(CarryInInfo info);

    /**
     * 반입 적치 제품 목록을 요청한다.
     * @param info 반입 적치 제품 목록 요청 정보
     * @return VErrors 참조
     */
    public int reqCarryInPileUpGoodsList(CarryInInfo info);

    /**
     * 반입 적치 확정을 요청한다.
     * @param info 반입 적치 확정 요청 정보
     * @return VErrors 참조
     */
    public int reqCarryInPileUpConfirm(CarryInInfo info);

    /**
     * 반출번호 목록을 요청한다.
     * @param info 반입번호 목록 요청 정보
     * @return VErrors 참조
     */
    public int reqCarryOutNumberList(CarryNumberInfo info);

    /**
     * 반출목록을 요청한다.
     * @param info 반출목록 요청 정보
     * @return VErrors 참조
     */
    public int reqCarryOutList(CarryOutInfo info);

    /**
     * 반출확정을 요청한다.
     * @param info 반출확정 요청 정보
     * @return VErrors 참조
     */
    public int reqCarryOutPickingConfirm(CarryOutInfo info);

    /**
     * 재고이동 목록을 요청한다.
     * @param info 재고이동 목록을 요청 정보
     * @return VErrors 참조
     */
    public int reqInvenMoveList(InvenMoveInfo info);

    /**
     * 재고이동 확정 요청한다.
     * @param info 재고이동 정보
     * @return VErrors 참조
     */
    public int reqInvenMoveConfirm(InvenMoveInfo info);

    /**
     * 임의재고이동 지시 내용 요청한다.
     * @param info 임의재고이동 정보
     * @return VErrors 참조
     */
    public int reqInvenTempMoveSearch(InvenMoveInfo info);

    /**
     * 임의재고이동 LOC를 요청한다.
     * @param info 임의재고이동 LOC를 요청정보
     * @return VErrors 참조
     */
    public int reqInvenTempMoveLocSearch(InvenMoveInfo info);

    /**
     * 임의재고이동 확정 요청한다.
     * @param info 임의재고이동 정보
     * @return VErrors 참조
     */
    public int reqInvenTempMoveConfirm(InvenMoveInfo info);

    /**
     * 재고조사 목록을 요청한다.
     * @param info 재고조사 정보
     * @return VErrors 참조
     */
    public int reqInvenInquiryList(InvenInquiryInfo info);

    /**
     * 재고조사 확정을 요청한다.
     * @param info 재고조사 정보
     * @return VErrors 참조
     */
    public int reqInvenInquiryConfirm(InvenInquiryInfo info);

    /**
     * 재고 신규 등록을 요청한다.
     * @param info 재고 신규 등록 정보
     * @return VErrors 참조
     */
    public int reqInvenCreate(InvenInquiryInfo info);

    /**
     * 재고 신규 등록 제품 조회를 요청한다.
     * @param info 재고 신규 등록 정보
     * @return VErrors 참조
     */
    public int reqInvenCreateSearch(InvenInquiryInfo info);

    /**
     * 재고상태 변경 목록을 요청한다.
     * @param info 재고상태 변경 목록 요청 정보
     * @return VErrors 참조
     */
    public int reqInvenStateChangeList(InventoryStateChangeInfo info);

    /**
     * 재고상태를 변경 요청한다.
     * @param info 재고 상태 변경 정보
     * @return VErrors 참조
     */
    public int reqInvenStateChangeConfirm(InventoryStateChangeInfo info);

    /**
     * 재고관리 파렛트 분할 목록을 요청한다.
     * @param info 재고관리 파렛트 분할 정보
     * @return VErrors 참조
     */
    public int reqInvenPalletDivideList(InvenPltInfo info);

    /**
     * 재고관리 파렛트 분할을 요청한다.
     * @param info 재고관리 파렛트 분할 정보
     * @return VErrors 참조
     */
    public int reqInvenPalletDivide(InvenPltInfo info);

    /**
     * 재고관리 파렛트 병합을 요청한다.
     * @param info 재고관리 파렛트 병합 정보
     * @return VErrors 참조
     */
    public int reqInvenPalletMerge(InvenPltInfo info);

    /**
     * 제품 목록을 요청한다.
     * @param info 제품정보
     * @return VErrors 참조
     */
    public int reqGoodsInquiry(InquiryInfo info);

    /**
     * 제품 위치 목록을 요청한다.
     * @param info 제품정보
     * @return VErrors 참조
     */
    public int reqGoodsLocInquiry(InquiryInfo info);
}
