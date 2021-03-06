package com.web.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.curation.dao.KakaoDao;
import com.web.curation.dto.UserDto;

@Service
public class KakaoServiceImpl implements KakaoService {

	@Autowired
	private KakaoDao kakaoDao;

	@Override
	public Boolean isEmail(String email) {
		return kakaoDao.isEmail(email);
	}

	@Override
	public void insertKakao(UserDto userDto) {
		kakaoDao.insertKakao(userDto);
	}

	@Override
	public void updateKakao(UserDto userDto) {
		kakaoDao.updateKakao(userDto);
	}

	@Override
	public void deleteKakao(String email) {
		kakaoDao.deleteKakao(email);
	}

}
