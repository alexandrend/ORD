import java.util.HashMap;
import java.util.Map;


public class TestaHash {

	public static void main( String args[] ) {
		
		Map<String, Integer> m = new HashMap<String, Integer>();
		String cidades[] = {"João Pessoa", "Patos", "Campina Grande", "Bayeux"};
		
		for( int i = 0; i < 4; i++)
			m.put(cidades[i].toLowerCase(), new Integer(i * 1000));
	
		System.out.println( m.get( "joão PESSOA".toLowerCase()));
		System.out.println( m.get("Patos".toLowerCase()));
		System.out.println( m.get("Bayeux".toLowerCase()));
		System.out.println( m.get("bayeux".toLowerCase()));
		System.out.println( m.get("baieux".toLowerCase()));
	
	}
	
	
}
