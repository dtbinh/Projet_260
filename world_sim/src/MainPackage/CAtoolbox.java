package MainPackage;



public class CAtoolbox {

	protected static void print (String __s)
	{
		System.out.print(__s);
	}
	
	protected static boolean[][] initialiseRegles(int __noCA)
	{
		boolean[][] rules = new boolean[8][4];
		
		// conditions -- dont forget: strong bit on the left
		for ( int i = 0 ; i != 8 ; i++ )
		{
			if ( ( i & 1 ) == 1 )
				rules[i][2] = true;
			if ( ( i & 2 ) == 2 )
				rules[i][1] = true;
			if ( ( i & 4 ) == 4 )
				rules[i][0] = true;
		}
		
		// outcome
		for ( int i = 1 , j = 0 ; i != 256 ; i*=2, j++ )
		{			
			if ( ( __noCA & i ) > 0 )
				rules[j][3] = true;
			else
				rules[j][3] = false;
		}
		
		return rules;
	}
	
}
