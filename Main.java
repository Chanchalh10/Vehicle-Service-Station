package vehicleServiceStation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main implements Serializable
{
	public static void displayCustomerList()
	{
		for(Customer c : ServiceStation.station.getCustList())
			System.out.println(c);
	}
	public static void displayVehicleList(Customer c)
	{
		for(String num :c.getVehicleList().keySet())
			System.out.println(c.getVehicleList().get(num));
	}
	
	public static int getMainMenu()
	{
		System.out.println("0. Exit");
		System.out.println("1. New Customer");
		System.out.println("2. New Servicing Request");
		System.out.println("3. Today's Business");
		System.out.println("4. Given Day's Service Request");
		System.out.print("Enter Your Choice: ");
		return sc.nextInt();
	}
	
	public static int getPartMenu()
	{
		System.out.println("0. Maintenance Done");
		System.out.println("1. Add New Part");
		System.out.print("Enter Your Choice: ");
		return sc.nextInt();
	}
	
	public static int getSubMenu()
	{
		System.out.println("0. Servicing Done");
		System.out.println("1. Oil Servicing");
		System.out.println("2. Maintenance Servicing");
		System.out.print("Enter Your Choice: ");
		return sc.nextInt();
	}
	public static void inputCustomer()
	{
		sc.nextLine();
		Customer c=new Customer();
		System.out.print("Enter Customer Name: ");
		c.setName(sc.nextLine());
		System.out.print("Enter Customer Mobile No.: ");
		c.setMobile(sc.nextLine());
		System.out.print("Enter Customer Address: ");
		c.setAddress(sc.nextLine());
		System.out.print("Enter Customer Last Balance: ");
		c.setLastBalance(sc.nextDouble());
		
		ServiceStation.station.newCustomer(c); 
	}
	public static void inputMaintenance(MaintenanceService obj)
	{
		System.out.print("Enter Description: ");
		obj.setDesc(sc.next());
		System.out.print("Enter price: ");
		obj.setLaborCharges(sc.nextDouble());
	}
	
	public static void inputMaintenanceParts(MaintenanceService obj)
	{
		System.out.print("Enter Part Name: ");
		String name=sc.next();
		System.out.print("Enter Part Price: ");
		obj.newSparePart(new SparePart(name,sc.nextDouble()));
	}
	public static void inputOilService(OilService obj)
	{
		System.out.print("Enter Description ");
		obj.setDesc(sc.next());
		System.out.print("Enter  Price ");
		obj.setCost(sc.nextDouble());
	}
	
	public static Vehicle inputVehicle(Customer c)
	{
		Vehicle v=new Vehicle();
		System.out.print("Enter Vehicle Company: ");
		v.setCompany(sc.next());
		System.out.print("Enter Vehicle Model: ");
		v.setModel(sc.next());
		System.out.print("Enter Vehicle Number: ");
		v.setNumber(sc.next());
		System.out.println(v);
		c.newVehicle(v);
		return v;
	}
	
	
	
	public static void displayServiceRequestsByDate(Date date) {
        List<ServiceRequest> requests = ServiceStation.station.getServiceRequestsByDate(date);
        if (requests.isEmpty()) {
            System.out.println("No service requests found for this date.");
        } else {
            for (ServiceRequest request : requests) {
                System.out.println(request);
            }
        }
    }
	public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
	
		int ch;
		ServiceStation.station.loadBillDetails();
		ServiceStation.station.loadCustDetails();
		System.out.println(ServiceStation.station.name);
		System.out.println(ServiceStation.station.getCustList());
		while((ch=getMainMenu())!=0)
		{
			switch(ch)
			{
			case 1:
				System.out.println("New Customer");
				inputCustomer();
				displayCustomerList();
				System.out.println("New Customer Added");
				break;
			case 2:
				System.out.println("New Servicing Request");
				System.out.println("Enter Customer Name: ");
				Customer c;
				if((c=ServiceStation.station.findCustomer(sc.next()))==null)
				{
					System.out.println("Customer Not Found!!!");
					break;
				}
				System.out.println("Customer Found!!!");
				System.out.println(c);
				int o=1;
				if(c.getVehicleList().isEmpty())
					System.out.println("Add New Vehicle");
				else
				{
					System.out.print("Enter 0. Add New Vehicle\n1. Select Existing Vehicle");
					o=sc.nextInt();
				}
				Vehicle v=null;
				if(o==0||c.getVehicleList().isEmpty())
					v=inputVehicle(c);
				else
				{
					displayVehicleList(c);
					System.out.print("Enter Vehicle No.: ");
					v=c.getVehicle(sc.next());
					if(v==null)
					{
						System.out.println("Vehicle Not Found!!!");
						break;
					}
					System.out.println(v);
				}
				ServiceRequest r=ServiceStation.station.newRequest(c.getName(), v.getNumber());
				int ch1;
				while((ch1=getSubMenu())!=0)
				{
					switch(ch1)
					{
					case 1:
						OilService oil=new OilService();
						inputOilService(oil);
						r.newService(oil);
						break;
					case 2:
						MaintenanceService mser=new MaintenanceService();
						inputMaintenance(mser);
						int ch2;
						while((ch2=getPartMenu())!=0)
						{
							switch(ch2)
							{
								case 1:
									inputMaintenanceParts(mser);
									break;
								default:
										System.out.println("Enter Correct Choice!!!");
							}
						}
						r.newService(mser);
						break;
					default:
						System.out.println("Enter Correct Choice!!!");
					}
				}
				Bill b=ServiceStation.station.newBill(r);
				b.print(System.out);
				System.out.print("Enter Paid Amount: ");
				System.out.println("Remaining Amount: "+c.payBill(b, sc.nextDouble()));
				break;
			case 3:

				
				System.out.println("Todays Cash: "+ServiceStation.station.computeCash(new Date()));
				break;
			case 4:
				  System.out.print("Enter Date (dd/MM/yyyy): ");
                  String dateString = sc.next();
                  Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
                  displayServiceRequestsByDate(date);
                  break;
			default:
				System.out.println("Enter Correct Choice!!!");	
			}
		}
		ServiceStation.station.storeBillDetails();
		ServiceStation.station.storeCustDetails();
	}
	


	    
	
	private static final long serialVersionUID = 1L;
	
	public static Scanner sc=new Scanner(System.in);

	public static String welcome() {
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(outBytes);
		out.printf("Java My Project @ SunBeam Infotech\n\n");
		out.printf("Vehicle Service Station\n\n");
		out.printf("This is simplified version of the case study implementation.\n");
		out.printf("Consider this as base and implement case study of your own.\n");
		out.printf(
				"This implementation should help you to understand requirements better. First read the given documentation of case study.\n\n");
		out.printf("However most of the validations are not implemented. So be careful while trying it.\n");
		out.printf("Note that all string inputs are considered without white space.\n");
		out.printf("Given a dummy customer database as sample and is used in this program.\n");
		out.flush();
		String text = outBytes.toString();
		out.close();
		return text;
	}
	

	
	
}