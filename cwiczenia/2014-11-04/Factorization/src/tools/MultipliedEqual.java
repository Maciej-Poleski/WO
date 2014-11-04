package tools;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class MultipliedEqual extends TypeSafeMatcher<List<Long>> {
	
	private long result;

	public MultipliedEqual(Long result)
	{
		this.result=result;
	}

	@Override
	public void describeTo(Description description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean matchesSafely(List<Long> item) {
		long r=1;
		for(long i: item)
			r*=i;
		return r==result;
	}
	
	public static MultipliedEqual isMultipliedEqual(long r)
	{
		return new MultipliedEqual(r);
	}

}
