package org.spoofax.interpreter.library.language.spxlang.tests;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;


/**
 * Demonstrates more advanced usage of JDBM:
 * Secondary maps, 1:N relations.
 * 
 * @author Jan Kotek
 *
 */
class MultiValuePersistentTable1 implements Serializable{
	private static final long serialVersionUID = -5921814375219835440L;
	private final HashMap<String, List<Address>> symbols;

	public MultiValuePersistentTable1(){
		symbols = new HashMap<String, List<Address>>();
	}
	
	/**
	 * Removes all the entries from this symbol-table
	 * 
	 * @throws IOException 
	 */
	public void clear(){ symbols.clear(); }

	
	/**
	 * Defines symbol in the current symbol table. Define does not replace  
	 * old symbol mapped using the key with the new one. It just adds the 
	 * new symbol at the end of the multivalue-list. 
	 * 
	 * @param key - The key that the symbol will be mapped to .
	 * @param symbol - The symbol to store. 
	 */
	public void define(String key , Address entry){
		if ( symbols.containsKey(key)){
			symbols.get(key).add(entry);
		}else{
			List<Address> values = new ArrayList<Address>(); 
			values.add(entry);
			symbols.put( key , values );
		}
	}
	
	public List<Address> resolve(String key){
		
		
		List<Address> resolvedSymbols = symbols.get(key);
		
		return (resolvedSymbols == null) ? new ArrayList<Address>() : resolvedSymbols ; 
	}
	
	public void logEntries() throws IOException{
		for( String  k : symbols.keySet()) {
			System.out.println("\t"+k.toString()  + " :  ");
			
			for( Address s : symbols.get(k) ){
				System.out.println( "\t\t"+ s.toString() + "");
			}
		}
		System.out.println();
	}
}

class PersonBase  implements Serializable
{
	private static final long serialVersionUID = 8846122082882116001L;

	/** field used for person identification (primary key)**/
	public String name;
	
	String fatherName;
	public PersonBase(String name, Address adress,String fatherName) {
		this.name = name;
		this.fatherName = fatherName;
	}
	
	 MultiValuePersistentTable1 members = new MultiValuePersistentTable1();
	
	public MultiValuePersistentTable1 getMembers() { return members;}
}

class Person extends PersonBase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5872886463794139334L;

	/** constructor, getters and setters are excluded for simplicity */
	public Person(String name, Address adress,String fatherName) {
		super(name, adress, fatherName);
	}
	
	//private MultiValuePersistentTable1 members = new MultiValuePersistentTable1();
	
//	@Override
//	public MultiValuePersistentTable1 getMembers() { return members;}
	
	public String toString(){
		return "Person["+name+"]";
	}
	
	public int hashCode() {
		return name == null? 0 : name.hashCode();
	}


	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Person))
			return false;
		Person other = (Person) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public void setAddresses() 
	{
		addAddress(new Address("First street1", "Athlone","Ireland"));
		addAddress(new Address("First street2", "Athlone","Ireland"));
		addAddress(new Address("First street3", "Athlone","Ireland"));
		
	}
	
	private void addAddress( Address address){
		this.getMembers().define("address", address);
		
	}
}

class Address implements Serializable{
	@Override
	public String toString() {
		return "Address [streetName=" + streetName + ", town=" + town + ", country=" + country + "]";
	}

	String streetName;
	String town;
	String country;
	
	public Address(String streetName, String town, String country) {
		super();
		this.streetName = streetName;
		this.town = town;
		this.country = country;
	}
	
	public String getId() { return streetName + "_" + town + "_" + country ;}
	
	
}

public class JdbmApiTests {
	public static void main(String[] args) throws IOException {
		String recDbName  = "personDBxx1234665";
		//init Record Manager and dao
		RecordManager recman = RecordManagerFactory.createRecordManager(recDbName);
		
		PrimaryHashMap<String,Person> personsByName = recman.hashMap("personsByName");
		if(personsByName.size() > 0)
			personsByName.clear();
		
		//create a few persons
		Person patrick = new Person("Patrick Moore", 
				new Address("First street", "Athlone","Ireland"),
				null);
		patrick.setAddresses(); 
		personsByName.put(patrick.name, patrick);
		
		patrick = new Person("Patrick Moore2", 
				new Address("First street", "Athlone","Ireland"),
				null);
		patrick.setAddresses(); 
		personsByName.put(patrick.name, patrick);
		recman.commit();
		
		personsByName.get("Patrick Moore").getMembers().logEntries();
		recman.close();
		recman = null;
		
		RecordManager recman2= RecordManagerFactory.createRecordManager(recDbName);
		PrimaryHashMap<String,Person> personsByName2 = recman2.hashMap("personsByName");
		
		System.out.println("Number of persons: "+personsByName2.size());
		
		
		
		System.out.println("Persons with name Patrick Moore: "+personsByName2.get("Patrick Moore"));
		
		Person p = personsByName2.get("Patrick Moore");
		
		System.out.println("Found : " +p.name); 
		p.getMembers().logEntries();
		
		recman2.close();
	}
	
}
