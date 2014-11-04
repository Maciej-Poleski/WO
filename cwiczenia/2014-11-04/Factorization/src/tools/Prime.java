package tools;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;


public class Prime extends TypeSafeMatcher<Long>
{
	
	public Prime() {
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("prime number");
		
	}

	@Override
	protected boolean matchesSafely(Long item) {
		for(long i=2;i<item;++i)
			if(item%i==0)
				return false;
		return true;
	}
	
	public static Prime isPrime()
	{
		return new Prime();
	}
}