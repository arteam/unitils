package org.unitils.parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.UnitilsParameterized;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;


/**
 * ParameterizedIntegrationTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4 
 */
@RunWith(UnitilsParameterized.class)
public class ParameterizedIntegrationTest {

    @Mock
    PlayerDao playerDao;
    
    @Mock
    TrophyDao trophyDao;
    
    ChampionshipService service;
    Trophy trophy;
    
    @Before
    public void setUp(){
        service = new ChampionshipService();
        service.setPlayerDao(playerDao);
        service.setTrophyDao(trophyDao);
        trophy = new Trophy();
    }
    
    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { 1 }, { 2 }, { 3 }, { 4 } };
        return Arrays.asList(data);
    }
    
    private int number;
    
    /***/
    public ParameterizedIntegrationTest(int number) {
        this.number = number;
    }
    
    /***/
    @Test
    public void testChampionService() {
        EasyMock.expect(trophyDao.findByChampionshipsName("spi")).andReturn(trophy);
        EasyMock.expect(playerDao.getPlayersHavingTrophy(trophy)).andReturn(null);
        
        EasyMockUnitils.replay();
        service.winnersInChampionship("spi");
        EasyMock.verify(trophyDao);
        EasyMock.verify(playerDao);
        
    }
    
    @Ignore
    public void testWrongChampionService() {
        EasyMock.expect(trophyDao.findByChampionshipsName("spi")).andReturn(trophy);
        EasyMock.expect(playerDao.getPlayersHavingTrophy(trophy)).andReturn(null);
        EasyMock.expect(trophyDao.findByChampionshipsName("spi")).andReturn(trophy);
        EasyMock.expect(playerDao.getPlayersHavingTrophy(trophy)).andReturn(null);
        EasyMock.expect(trophyDao.findByChampionshipsName("spi")).andReturn(trophy);
        EasyMock.expect(playerDao.getPlayersHavingTrophy(trophy)).andReturn(null);
        
        EasyMockUnitils.replay();
        service.winnersInChampionship("spi");
        EasyMock.verify(trophyDao);
        EasyMock.verify(playerDao);
    }
    
    //test classes
    /***/
    private class ChampionshipService {
        private PlayerDao playerDao1 ;
        private TrophyDao trophyDao1 ;
        /**
         * @param championship 
         * @return {@link List}*/
        public List<Player> winnersInChampionship (String championship ){
            Trophy trophy1 = getTrophyDao1().findByChampionshipsName(championship);
            List<Player> playersThrophy = getPlayerDao1().getPlayersHavingTrophy(trophy1);
            
            return playersThrophy ;
        }

        public PlayerDao getPlayerDao1() {
            return playerDao1;
        }

        public void setPlayerDao(PlayerDao playerDao1) {
            this.playerDao1 = playerDao1;
        }

        public TrophyDao getTrophyDao1() {
            return trophyDao1;
        }

        public void setTrophyDao(TrophyDao trophyDao1) {
            this.trophyDao1 = trophyDao1;
        }
    }
    
    private interface PlayerDao {

        /**
         * @param trophy1
         * @return {@link List}
         */
        public List<Player> getPlayersHavingTrophy(Trophy trophy1);
        
    }
    private interface TrophyDao {

        /**
         * @param championship 
         * @return {@link Trophy}
         */
        public Trophy findByChampionshipsName(String championship);
        
    }
    private class Player {
        //just a test class
    }
    private class Trophy {
        //just a testclass
    }

    
}
