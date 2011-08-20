package jdbm.test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

 /**
 * This program demonstrates basic JDBM usage. Updated to support separate-chaining using PrimaryMap.
 * 
 * @author Jan Kotek
 * @author Adil Akhter
 */
public class HelloWorld {
	
	public static void put( Map<Integer,LinkedList<String>> hashmap , int key, String value )
	{
		if (hashmap.containsKey(key))
		{
			hashmap.get(key).add(value);
		}		
		else
		{
			LinkedList<String> l = new LinkedList<String>();
			l.add(value);
			
			hashmap.put(key, l);
		}
	}
	
	public static void main(String[] args) throws IOException {

		/** create (or open existing) database */
		String fileName = "helloWorld2";
		RecordManager recMan = RecordManagerFactory.createRecordManager(fileName);
		
		/** Creates TreeMap which stores data in database.  
		 *  Constructor method takes recordName (something like SQL table name)*/
		String recordName = "firstTreeMap2";
		Map<Integer,LinkedList<String>> hashmap = recMan.hashMap(recordName); 

		hashmap.clear();
		
		recMan.commit();
		
		/** add some stuff to map*/
		put(hashmap, 1, "One");
		put(hashmap, 1, "Test");
		put(hashmap, 2, "Two");
		put(hashmap, 3, "Three");
		put(hashmap, 3, "Four");
		put(hashmap, 3, "Five");
		
		System.out.println(hashmap.keySet());
		System.out.println(hashmap.values());
		// > [1, 2, 3]
		
		/** Map changes are not persisted yet, commit them (save to disk) */
		recMan.commit();
		System.out.println("After committing: ");	
		System.out.println(hashmap.keySet());
		// > [1, 2, 3]

		/** Delete one record. Changes are not commited yet, but are visible. */
		hashmap.remove(2);
		System.out.println("After removing 2: ");	
		System.out.println(hashmap.keySet());
		// > [1, 3]
		
		/** Did not like change. Roolback to last commit (undo record remove). */
		recMan.rollback();
		
		System.out.println("After rollback: ");
		/** Key 2 was recovered */
		System.out.println(hashmap.keySet());
		// > [1, 2, 3]
		
		/** close record manager */
		recMan.close();
		
		
	}
	
	
	
}
