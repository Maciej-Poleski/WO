import java.lang.Math;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Factorization
{
    public static List<Long> primeFactors(long n)
    {
		if (n < 0)
			n = -n;
		if (n == 0)
			throw new InvalidParameterException();
        List<Long> factors = new ArrayList<Long>();
		for (long p = 2; p < Math.sqrt(n); ++p)
			if (n % p == 0)
            {
                factors.add(p);
				n = n / p;
            }
        factors.add(n);
		return factors;	
    }

	public static long smallestPrimeFactor(long n)
	{
        return primeFactors(n).get(0);
	}
}
