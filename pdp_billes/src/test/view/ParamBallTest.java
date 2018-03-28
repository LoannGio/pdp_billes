package test.view;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import view.AParamObject;
import view.ParamBall;

public class ParamBallTest {
	ParamBall pb;

	@Before
	public void setUp() throws Exception {
		pb = new ParamBall(null, null);
	}

	@After
	public void tearDown() throws Exception {
		pb = null;
	}

	@Test
	public void test_checkInt() {
		Boolean result;
		try {
			Method checkInt = AParamObject.class.getDeclaredMethod("checkInt", String.class);
			checkInt.setAccessible(true);
			/* Tests de valeurs rejetees par la fonction checkInt */
			result = (Boolean) checkInt.invoke(pb, "a");
			assertEquals(false, result);
			result = (Boolean) checkInt.invoke(pb, "10.0");
			assertEquals(false, result);
			// Notre fonction checkInt n'accepte pas les nombres négatifs
			result = (Boolean) checkInt.invoke(pb, "-1");
			assertEquals(false, result);
			result = (Boolean) checkInt.invoke(pb, "");
			assertEquals(false, result);

			/* Test de valeur acceptee par la fonction checkInt */
			result = (Boolean) checkInt.invoke(pb, "1");
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
			/* Tests de valeurs rejetees par la fonction checkDouble */
			result = (Boolean) checkDouble.invoke(pb, "a");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pb, "1.");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pb, "1.1.");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pb, ".1");
			assertEquals(false, result);
			// Notre fonction checkDouble n'accepte pas les nombres négatifs
			result = (Boolean) checkDouble.invoke(pb, "-1");
			assertEquals(false, result);
			result = (Boolean) checkDouble.invoke(pb, "");
			assertEquals(false, result);

			/* Tests de valeurs acceptees par la fonction checkDouble */
			result = (Boolean) checkDouble.invoke(pb, "10.0");
			assertEquals(true, result);
			result = (Boolean) checkDouble.invoke(pb, "1");
			assertEquals(true, result);
			result = (Boolean) checkDouble.invoke(pb, "1.1");
			assertEquals(true, result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
