package project.logic;

import project.object.Airport;
import project.object.Booking;
import project.object.Companies;
import project.object.Flights;
import project.object.Planes;
import project.object.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.db.*;

public class LoginLogic {
	DbAccessImpl db = new DbAccessImpl();
	
	//method to allow user sign up shows how calls should work
	public boolean signUp(User u) {
		
		//set up query for creating a new user
		String query = "Insert into login(firstname, lastname, upass, uemail, isadmin) values " +
						"( \"" + u.getFirstName() + "\" , \"" + u.getLastName() + "\" , \"" + u.getPassword() +
						"\" , \"" + u.getEmail() + "\" , 0);";
		
		//db stuff call connect and disconnect before and after 
		db.connect();
		int j = db.create(query);
		db.disconnect();
		
		//if success create of one user return true in the program
		if(j == 1) {
			return true;
		}
		
		//did nto successfully create a user
		else
			return false;
	}
	
	
	public String login(String user, String pass) {
		//TODO login logic should return user id to be used in session tracking
		String query = "select uid from login where " +
					"uemail = \"" + user + "\"  and upass = \"" + pass + "\";";
		
		String userID = null;
		db.connect();
		ResultSet rs = null;
		
		rs = db.retrieve(query);
		
		
		try {
			while(rs.next()) {
				userID = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.disconnect();
		return userID;
		
	}
	

	public String[] getFirstLast(String id) {
		//Returns a string array that contains the first and last name
		String[] names = new String[2];
		db.connect();
		ResultSet rs = db.retrieve("Select firstname,lastname from login where uid='"+id+"';");
		try {
			rs.next();
			names[0] = rs.getString("firstname");
			names[1] = rs.getString("lastname");
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("UID might not exist");
		}
		db.disconnect();
		return names;
	}
	
		public List<Flights> rtnFlightList(String dep, String des, String date){

		db.connect();
		
		List<Flights> lf = new ArrayList<>();
		String query = "select * from flights where " +
				"airportDES = (select airportid from airport where citytag = \"" + des + "\") and " +
				"airportDEP = (select airportid from airport where citytag =\"" + dep + "\") and " +
				"INSTR(departureTime, \""  + date +  "\");";
		
		
		
		System.out.println(query);
		ResultSet rs = null;
		
		System.out.println(query);
		rs = db.retrieve(query);
		try {
			while(rs.next()) {
				try {
					//create new flight object rs.getString(1) = id
					//rs.getDate(7) = departure date
					//rs.getInt(5) - rs.getInt(6) = ttlseats - bookedseats == available seats
					Flights f = new Flights(rs.getString(1), rs.getString(7), rs.getInt(5) - rs.getInt(6), rs.getString(9));
					lf.add(f);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.disconnect();
		//return list of flight objects here
		return lf;
		
	}
	//this thing is confusing and may or may not cause issues not sure yet. 
	public boolean createFlight(Flights f) {
		//TODO create a function to insert a flight in database return if the create worked or not
		//I'm just writting a function to get ids for this and planes b/c man
		
		int departure = getAirportId(f.departure);
		int destination = getAirportId(f.destination);
		int compid = getCompId(f.companyName);
		
		String query = "Insert into Flights(airportDes, airportDep, planeid, ttlseatsonplane, departureTime, arivalTime) values"
				+ "\""+ destination + "\",  \"" + departure + "\" , \"" +  compid + "\" , \"" + f.ticketsavialable + "\" , \"" + f.departureDate + "\" , \"" + f.destinationDate + "\");";
	
		return true;
	}
	
	public boolean createPlane(Planes p) throws SQLException {
		//first we have to get the compid because I thought that was a good idea 
		//DONE
		
		String getCompid = "select compid from planecomp " +
							"where compname = \"" + p.getCompname() + "\";";
		
		db.connect();
		int compid = 0;
		ResultSet rs = null;
		rs = db.retrieve(getCompid);
		while(rs.next()) {
			compid = rs.getInt(1);
		}
		
		String query = "insert into planes (compid, ttlseats, planetype, inService) values(" + compid + ", " + p.getTtlseats() + ", \"" + p.getPlanetype() + "\", 1);";
		
		
		int i = db.update(query);
		
		if(i > 0) {
		
		return true;
		}
		else return false;
		}
	
	public boolean createCompany(Companies c) {
		//this is an easier function DONE
		String query = "INSERT INTO PLANECOMP(COMPNAME) VALUES ( \"" + c.getCompName() + "\" );";
		
		db.connect();
		int i = db.create(query);
		db.disconnect();
		if(i > 0) {
		return true;
		}
		else return false;
	}
	public boolean createBooking(Booking b) {
		//Insert a booking query returns same values as every other create I make unfortunately
		//this isn't exciting stuff
		String  query = "Insert into bookedflights(uid, flightid, Amountbooked) values "+
		 "( \"" + b.getUid() + "\" , \"" + b.getFlightid() + "\" , \"" + b.getAmountbooked() + "\");";
		db.connect();
		int i = db.create(query);
		
		db.disconnect();
		if(i > 0) {
		
		
		return true;
		}
		
		else return false;
	}
	public boolean createAirport(Airport a) {
		//DONE
		String query = "Insert into airport(citytag, statetag, countrytag) values " +
						"( \"" + a.getCity() + "\" , \"" + a.getState() + "\" , \"" + a.getCountry() + "\" );";
		
		db.connect();
		int i = db.create(query);
		db.disconnect();
		if(i > 0) 
		{
		return true;
		}
		else return false;
	}
	public List<Booking> getBookings(String uid) throws SQLException{
		//DONE
		
		String query = "select * from bookedflights where "+
		"uid = \"" + uid + "\";";
		ResultSet rs = null;
		db.connect();
		List<Booking> lb = new ArrayList<>();
		rs = db.retrieve(query);
		while(rs.next()) {
			Booking b = new Booking(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
			lb.add(b);
		}
		
		return lb;
	}
	
	public boolean updatePassword(User u) {
		//TODO create ability for user to update his password (Low priority)
		
		return true;
	}
	
	public List<String> getCompanies() throws SQLException
	{
		//have to return company names otherwise stuff doesn't work right
		List<String> complist = new ArrayList<>();
		db.connect();
		String query = "select planecomp.compname from planecomp;";
		ResultSet rs = null;
		rs = db.retrieve(query);
		while(rs.next()) {
			complist.add(rs.getString(1));
		}
		
		db.disconnect();
		
		
		//return list of all companies.
		return complist;
		
	}
	
	public boolean updateSeatCount(int seatsbought, int flightid) throws SQLException
	{
		//mfw I realize I have to update seat count probably at some point
		int j = -1;
		db.connect();
		String query = "select ttlseatsbooked from flights where flightid = \"" + flightid + "\"";
		ResultSet rs = null;
		rs = db.retrieve(query);
		int i = -1;
		while(rs.next()) {
			i = rs.getInt(1);
		}
		i += seatsbought;
		query = "update flights set ttlseatsbooked = " + i + " where flightid = " + flightid + ";";
		j = db.update(query);
		
		db.disconnect();
		
		if(j == 1) {
			return true;
		}
		
		else return false;
	}
	
	
	public List<String> getAirports(String city) throws SQLException
	{
	//hey you'll need me later gj realizing this early
		
		//this query checks the starting digit should work with AJAX call so it's selecting whatever you begin typing i.e 
		//"A" should return atlanta and austin but then At will only return atlanta
		String query = "select citytag from airports" +
	" where citytag like \"" + city + "%\";";
		db.connect();
		
		List<String> myAirportList = new ArrayList<>();
		ResultSet rs = null;
		rs = db.retrieve(query);
		
		while(rs.next()) {
			myAirportList.add(rs.getString(1));
			
		}
		db.disconnect();
		return myAirportList;					
	}
	
	public int getAirportid(String airportname) {
		//TODO function to get an airport id realized I needed this function to clean code :') feels bad man
		int airportid = -1;
		
		db.connect();
		String query = "select airportid from airport where citytag = \"" + airportname + "\"";
		ResultSet rs = null;
		rs = db.retrieve(query);
		try {
			airportid = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.disconnect();
		return airportid;
		
		
	}
	
	
	public int getCompId(String name) {
		String getCompid = "select compid from planecomp " +
				"where compane = \"" + name + "\";";

		db.connect();
		int compid = 0;
		ResultSet rs = null;
		rs = db.retrieve(getCompid);
		while(rs.next()) {
				compid = rs.getInt(1);
				
		}
		return compid;
}

	}

	
	
	
}
//end of method stub
