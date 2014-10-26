package com.nyankosama.test;

import com.nyankosama.nio.net.utils.BindFunction;
import com.nyankosama.nio.net.utils.ObjectBuffer;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by hlr@superid.cn on 2014/10/26.
 */
public class TestObjectBuffer {
    private static class Wrap {
        public TestObj constructObj(int num) {
            return new TestObj(num);
        }

        public void reset(TestObj testObj) {
            testObj.setNum(0);
        }
    }

    private static class TestObj {
        private int num;

        public TestObj(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

    }

    private ObjectBuffer<TestObj> objObjectBuffer;
    private Wrap wrap = new Wrap();

    @Before
    public void setUp() {
        BindFunction<TestObj> create = BindFunction.bind(wrap, "constructObj", 0);
        BindFunction reset = BindFunction.bind(wrap, "reset");
        objObjectBuffer = new ObjectBuffer<>(create, reset);
    }

    @Test
    public void testGet(){
        int size = objObjectBuffer.size();
        System.out.println("size= " + size);
        TestObj obj = objObjectBuffer.getObject();
        obj.setNum(2);
        assert objObjectBuffer.size() == size - 1;
        objObjectBuffer.returnObject(obj);
        assert objObjectBuffer.size() == size;
        obj = objObjectBuffer.getObject();
        assert obj.getNum() == 0;
        int oldCapacity = objObjectBuffer.capacity();
        System.out.println(oldCapacity);
        for (int i = 0; i < size + 5; i++) {
            objObjectBuffer.getObject();
        }
        System.out.println(objObjectBuffer.capacity());
    }
}
