package jdbm.test;

import java.io.IOException;

import jdbm.InverseHashView;
import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

public class TestInverseHashMap {

	
	public static void main(String args[]) throws IOException
	{
		String managerName = "testmanager";
		RecordManager manager  = RecordManagerFactory.createRecordManager( managerName);
		PrimaryHashMap<String , String> primaryMap =  manager.hashMap("test");

		InverseHashView<String, String> primaryMapInverse = primaryMap.inverseHashView ( "testInverse");
		
		String test = "testString" ;
		
		primaryMap.put("1", test);
		primaryMap.put("2", test);
		primaryMap.put("4", test);
		
		manager.commit();
		manager.close();
		
		
		RecordManager manager2  = RecordManagerFactory.createRecordManager( managerName);
		PrimaryHashMap<String , String> primaryMap2 =  manager2.hashMap("test");

		
		System.out.println(primaryMap2.keySet());
		
		
	}
}
