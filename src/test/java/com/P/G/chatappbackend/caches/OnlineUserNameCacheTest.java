package com.P.G.chatappbackend.caches;

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

        onlineUserNameCache.addNewOnlineUser("Dave", sessionId);

        assertTrue(onlineUserNameCache.getNames().containsKey(sessionId));
    }

    @Test
    public void removeUserFromCache_test() {
        String sessionId = "12352";

        onlineUserNameCache.addNewOnlineUser("Dave", sessionId);

        onlineUserNameCache.removeUserFromCache(sessionId);

        assertFalse(onlineUserNameCache.getNames().containsKey(sessionId));
    }

    @Test
    public void getOnlineUsers_test() {
        String sessionId1 = "12352";
        String sessionId2 = "12352342";
        String name1 = "Candy";
        String name2 = "Richard";

        onlineUserNameCache.addNewOnlineUser(name1, sessionId1);
        onlineUserNameCache.addNewOnlineUser(name2, sessionId2);

        assertTrue(onlineUserNameCache.getOnlineUsers().getUsers().containsAll(Arrays.asList(name1, name2)));
    }
}
