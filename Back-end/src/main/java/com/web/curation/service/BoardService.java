package com.web.curation.service;

import java.util.List;

import com.web.curation.dto.BoardPK;
import com.web.curation.dto.BroadCastingDto;
import com.web.curation.dto.FollowDto;

public interface BoardService {

	/* 좋아요 클릭 */
	boolean goodClick(BoardPK boardPK);

	/* 좋아요 취소 */
	boolean goodClickCancel(BoardPK boardPK);

	/* 좋아요 수 표시 */
	int goodCount(BoardPK boardPK);


}
