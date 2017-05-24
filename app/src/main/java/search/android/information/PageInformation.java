package search.android.information;

/**
 * Created by nhnent on 2017. 5. 10..
 */

public class PageInformation {
    public static final int OK = 0;
    public static final int REQUEST_CANCLE = 1;
    public static final int NO_INTERNET = 5;
    public static final int NO_SEARCH_RESULT = 2;
    public static final int TIME_OUT = 3;
    public static final int NO_INTENT = 4;
    public static final int UNKOWN_ERROR = 100;

    public static String statusMessage(int status) {

        switch(status) {
            case OK :
                return "정상작동";
            case REQUEST_CANCLE :
                return "요청을 취소했습니다.";
            case NO_SEARCH_RESULT :
                return "검색결과가 존재하지 않습니다.";
            case TIME_OUT:
                return "수행시간을 초과했습니다.";
            case NO_INTENT :
                return "데이터가 존재하지 않습니다.";
            case NO_INTERNET :
                return "인터넷이 연결되지 않았습니다.";
            default :
                return "알 수 없는 상태입니다.";
        }
    }
}
