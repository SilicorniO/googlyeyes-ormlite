package com.silicornio.googlyeyessqlite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.j256.ormlite.stmt.Where;
import com.silicornio.geormlite.GeOrmLiteManager;
import com.silicornio.geormlite.general.GEL;
import com.silicornio.googlyeyessqlite.model.Item;
import com.silicornio.googlyeyessqlite.model.ItemSub;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GooglyEyesOrmLiteTests {

    Context mMockContext;
    DBManager mDbManager;

    @Before
    public void setUp() {

        GEL.showLogs = true;

        mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        mMockContext.deleteDatabase(DBManager.DATABASE_NAME);
        mDbManager = DBManager.getInstance(mMockContext);

    }

    private Item getItemFilled(String id){
        Item item = new Item();
        item.id = id;
        item.flag = true;
        item.text = "text";
        item.numberInt = 1;
        item.numberFloat = 2.2f;
        item.numberDouble = 3.3d;

        ItemSub itemSub = new ItemSub();
        itemSub.subText = "subtext";
        item.itemSub = itemSub;

        return item;
    }

    //----- TESTS -----

    @Test
    public void test001AddItem(){
        Item item = getItemFilled("1");
        mDbManager.add(item);
        Assert.assertTrue(true);
    }

    @Test
    public void test002GetFirstItem(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        Item itemDb = mDbManager.getFirst(Item.class);
        Assert.assertTrue(item.equals(itemDb));
    }

    @Test
    public void test003GetItemById(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        Item itemDb = mDbManager.getObjectById(Item.class, "1");
        Assert.assertTrue(item.equals(itemDb));
    }

    @Test
    public void test004GetItemByIdWithObject(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        Item itemDb = mDbManager.getObjectById(item);
        Assert.assertTrue(item.equals(itemDb));
    }

    @Test
    public void test005GetAllItems(){
        mDbManager.deleteAll(Item.class);
        Item item0 = getItemFilled("0");
        Item item1 = getItemFilled("1");
        mDbManager.add(item0);
        mDbManager.add(item1);
        List<Item> itemsDb = mDbManager.getAll(Item.class);
        Assert.assertTrue(itemsDb.size()==2);
    }

    @Test
    public void test006UpdateItem(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("0");
        mDbManager.add(item);
        item.numberFloat = 100;
        item.numberDouble = 100;
        item.numberInt = 100;
        item.flag = false;
        item.text = "testUpdate";
        mDbManager.update(item);
        Item itemDb = mDbManager.getObjectById(Item.class, "0");
        Assert.assertTrue(item.equals(itemDb));
    }

    @Test
    public void test007DeleteItem(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        mDbManager.delete(item);
        Item itemDb = mDbManager.getFirst(Item.class);
        Assert.assertTrue(itemDb==null);
    }

    @Test
    public void test101GetItemByFieldString(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        List<Item> itemsDb = mDbManager.getObjectsByFields(Item.class, "text=text");
        Assert.assertTrue(item.equals(itemsDb.get(0)));
    }

    @Test
    public void test102GetItemByFieldString(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        List<Item> itemsDb = mDbManager.getObjectsByFields(item, "text");
        Assert.assertTrue(item.equals(itemsDb.get(0)));
    }

    @Test
    public void test103GetItemByFieldStringAndInt(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        List<Item> itemsDb = mDbManager.getObjectsByFields(Item.class, "text=text,numberInt=1");
        Assert.assertTrue(item.equals(itemsDb.get(0)));
    }

    @Test
    public void test104GetItemByFieldStringAndInt(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        List<Item> itemsDb = mDbManager.getObjectsByFields(item, "text,numberInt");
        Assert.assertTrue(item.equals(itemsDb.get(0)));
    }

    @Test
    public void test105GetNumItemsByFieldDouble(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        long count = mDbManager.getNumObjectsByFields(Item.class, "numberDouble=3.3");
        Assert.assertTrue(count == 1);
    }

    @Test
    public void test106GetNumItemsByFieldDouble(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        long count = mDbManager.getNumObjectsByFields(item, "numberDouble");
        Assert.assertTrue(count == 1);
    }

    @Test
    public void test107GetNumItemsByClass(){
        mDbManager.deleteAll(Item.class);
        Item item = getItemFilled("1");
        mDbManager.add(item);
        long count = mDbManager.getNumObjects(Item.class);
        Assert.assertTrue(count == 1);
    }

    @Test
    public void test108GetFirstItemByFields(){
        mDbManager.deleteAll(Item.class);
        Item item1 = getItemFilled("1");
        item1.text = "text1";
        mDbManager.add(item1);
        Item item2 = getItemFilled("2");
        item2.text = "text2";
        mDbManager.add(item2);
        Item itemDb = mDbManager.getFirstObjectByFields(item2, "text");
        Assert.assertTrue(item2.equals(itemDb));
    }

    @Test
    public void test109GetFirstItemByFields(){
        mDbManager.deleteAll(Item.class);
        Item item1 = getItemFilled("1");
        item1.text = "text1";
        mDbManager.add(item1);
        Item item2 = getItemFilled("2");
        item2.text = "text2";
        mDbManager.add(item2);
        Item itemDb = mDbManager.getFirstObjectByFields(Item.class, "text=text2");
        Assert.assertTrue(item2.equals(itemDb));
    }

    @Test
    public void test201WhereEqualValue(){
        mDbManager.deleteAll(Item.class);
        Item item1 = getItemFilled("1");
        item1.text = "";
        mDbManager.add(item1);
        List<Item> itemsDb = mDbManager.executeQuery(Item.class, new GeOrmLiteManager.GeQueryImplementation() {
            @Override
            public void applyWhere(Where where) throws SQLException {
                where.eq("text", "");
            }
        });
        Assert.assertTrue(itemsDb.get(0).equals(item1));
    }


}
