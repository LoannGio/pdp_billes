package test.view;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import view.AParamObject;
import view.ParamLine;

public class ParamLineTest {
	ParamLine pl;

	@Before
	public void setUp() throws Exception {
		pl = new ParamLine(null, null);
	}

	@After
	public void tearDown() throws Exception {
		pl = null;
	}

	@Test
	public void test_checkInt() {
		Boolean result;
		try {
			Method checkInt = AParamObject.class.getDeclaredMethod("checkInt", String.class);
			checkInt.setAccessible(true);
			/* Testing checkInt rejected values */
			result = (Boolean) checkInt.invoke(pl, "a");
			assertEquals(false, result);
			result = (Boolean) checkInt.invoke(pl, "10.0");
			assertEquals(false, result);
			result = (Boolean) checkInt.invoke(pl, "-1");
			assertEquals(false, result);
			result = (Boolean) checkInt.invoke(pl, "");
			assertEquals(false, result);

			/* Testing checkInt accepted value */
			result = (Boolean) checkInt.invoke(pl, "1");
			assertEquals(true, result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test_checkDouble() {
		Boolean result;
		try {
			Method checkDouble = AParamObject.class.getDeclaredMethod("checkDouble", String.class);
			checkDouble.setAccessible(true);
			/* Testing checkDouble rejected values */
			result = (Boolean) checkDouble.invoke(pl, "a");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pl, "1.");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pl, "1.1.");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pl, ".1");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pl, "-1");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pl, "");
			assertEquals(false, result);

			/* Testing checkDouble accepted value */
			result = (Boolean) checkDouble.invoke(pl, "10.0");
			assertEquals(true, result);
			result = (Boolean) checkDouble.invoke(pl, "1");
			assertEquals(true, result);
			result = (Boolean) checkDouble.invoke(pl, "1.1");
			assertEquals(true, result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
