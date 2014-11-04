import static org.junit.Assert.*;
import static tools.Prime.isPrime;
import static tools.MultipliedEqual.isMultipliedEqual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.runner.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FactorizationTest {
	
	private long begin,end;
	
	public FactorizationTest(long begin, long end) {
		super();
		this.begin = begin;
		this.end = end;
	}

	@Parameters
	public static Collection<Object[]> params()
	{
		List<Object[]> result=new ArrayList<>();
		result.add(new Object[] {1,1000});
		
		return result;
	}
	
	private List<Long> tryToFactor(long n)
	{
		List<Long> factors=Factorization.primeFactors(n);
		assertThat(factors,isMultipliedEqual(n));
		assertThat(factors,everyItem(isPrime()));
		return factors;
	}
	
	private void calcTo(long n, long f)
	{
		for(long i=2;i<f;++i)
			assertNotEquals(0,n%i);
	}

	@Test
	public void testPrimeFactors() {
		for(long i=begin;i<=end;++i)
		{
			tryToFactor(i);
		}
	}

	@Test
	public void testSmallestPrimeFactor() {
		for(long i=begin;i<=end;++i)
		{
			long p=Factorization.smallestPrimeFactor(i);
			assertThat(p,isPrime());
			calcTo(i,p);
			assertEquals(0, i%p);
		}
	}

}
