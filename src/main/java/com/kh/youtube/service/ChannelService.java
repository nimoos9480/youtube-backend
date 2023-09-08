package com.kh.youtube.service;

import com.kh.youtube.domain.Channel;
import com.kh.youtube.domain.Member;
import com.kh.youtube.repo.ChannelDAO;
import com.kh.youtube.repo.MemberDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChannelService {

    @Autowired
    private ChannelDAO channelDAO;

    @Autowired
    private MemberDAO memberDAO;

    public List<Channel> showAll() {
        return channelDAO.findAll();
    }

    public Channel show(int id) {
        Channel channel = channelDAO.findById(id).orElse(null);  // 채널에 대한 정보
        Member member = memberDAO.findById(channel.getMember().getId()).orElse(null);  // int id를 member가 들고 있음
        channel.setMember(member); // 채널의 setMember에 member정보를 담기
        return channel;
    }

    public Channel create(Channel channel) {
        return channelDAO.save(channel);
    }

    public Channel update(Channel channel) {
        Channel target = channelDAO.findById(channel.getChannelCode()).orElse(null);
        if(target != null) {
            return channelDAO.save(channel);
        }
        return null;
    }

    public Channel delete(int id) {
        Channel target = channelDAO.findById(id).orElse(null);
        channelDAO.delete(target);
        return target;
    }

    // 특정 멤버의 모든 채널 조회
    public List<Channel> showMember(String id) {
        return channelDAO.findByMemberId(id);
    }



}
