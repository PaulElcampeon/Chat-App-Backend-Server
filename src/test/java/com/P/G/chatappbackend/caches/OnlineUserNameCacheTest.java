package com.P.G.chatappbackend.caches;

import com.P.G.chatappbackend.NameAndRoomIdHolder;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class OnlineUserNameCacheTest {

    @InjectMocks
    private OnlineUserNameCache onlineUserNameCache;

    @Test
    public void addNewOnlineUser_test() {
        String sessionId = "12352";
        NameAndRoomIdHolder nameAndRoomIdHolder = new NameAndRoomIdHolder("Dave", "test1");
        onlineUserNameCache.addNewOnlineUser(nameAndRoomIdHolder, sessionId);

        assertTrue(onlineUserNameCache.getNames().containsKey(sessionId));
    }

    @Test
    public void removeUserFromCache_test() {
        String sessionId = "12352";
        NameAndRoomIdHolder nameAndRoomIdHolder = new NameAndRoomIdHolder("Dave", "test1");

        onlineUserNameCache.addNewOnlineUser(nameAndRoomIdHolder, sessionId);

        onlineUserNameCache.removeUserFromCache(sessionId);

        assertFalse(onlineUserNameCache.getNames().containsKey(sessionId));
    }

    @Test
    public void getOnlineUsers_test() {
        String sessionId1 = "12352";
        String sessionId2 = "12352342";
        NameAndRoomIdHolder nameAndRoomIdHolder1 = new NameAndRoomIdHolder("Candy", "test1");
        NameAndRoomIdHolder nameAndRoomIdHolder2 = new NameAndRoomIdHolder("Richard", "test2");

        onlineUserNameCache.addNewOnlineUser(nameAndRoomIdHolder1, sessionId1);
        onlineUserNameCache.addNewOnlineUser(nameAndRoomIdHolder2, sessionId2);

        assertTrue(onlineUserNameCache.getNames().values().containsAll(Arrays.asList(nameAndRoomIdHolder1, nameAndRoomIdHolder2)));
    }
}
