package my.salonapp.salonbookingclientapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Home_Fragment extends Fragment implements OnClickListener  {

    // URL
    private static final String ASPX_GET_CLIENT_ALL_UPCOMINGBOOKINGS = "GetClientAllUpcomingBookings.aspx";
    private static final String ASPX_GET_ALL_COMPANIES = "GetAllCompanies.aspx";
    private static final String ASPX_GET_ALL_STAFFS = "GetAllStaffsForBooking.aspx";
    private static final String ASPX_GET_ALL_SERVICES = "GetAllSalonServices.aspx";

    // XML nodes for staff list
    private static final String XML_NODE_BOOKING_ID = "BookingID";
    private static final String XML_NODE_BOOKING_TIME = "BookingTime";
    private static final String XML_NODE_COMPANY_ID = "CompanyID";
    private static final String XML_NODE_COMPANY_NAME = "CompanyName";
    private static final String XML_NODE_STAFF_NAME = "StaffName";
    private static final String XML_NODE_STAFF_ID = "StaffID";
    private static final String XML_NODE_STAFF_COMPANY = "CompanyID";

    // XML nodes for service list
    private static final String XML_NODE_SERVICE_ID = "ServiceID";
    private static final String XML_NODE_SERVICE_NAME = "ServiceName";
    private static final String XML_NODE_SERVICE_PRICE = "ServicePrice";
    private static final String XML_NODE_SERVICE_DURATION = "ServiceDuration";

    // Control fields
    private View view;
    private ListView listView;
    private static Button newBookingBtn;
    private TextView welcomeUser;
    private static TextView logout;

    // Variables
    private Utils.Task task;
    private ClientDbHelper clientDb;
    private UpcomingBookingDbHelper db;
    private ArrayList<UpcomingBooking> bookings = new ArrayList<>();
    private UpcomingBooking booking;
    private CompanyDbHelper companyDb;
    private List<Company> companies = new ArrayList<>();
    private StaffDbHelper staffDb;
    private ArrayList<Staff> staffs = new ArrayList<>();
    private ServiceDbHelper serviceDb;
    private ArrayList<Service> services = new ArrayList<>();

    public Home_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);

        initViews();
        setListeners();
        showUser();
        refreshUpcomingBookingListData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Initiate Views
    private void initViews() {
        listView = view.findViewById(R.id.card_listView);
        newBookingBtn = view.findViewById(R.id.newBookingBtn);
        welcomeUser = view.findViewById(R.id.welcomeUser);
        logout = view.findViewById(R.id.logout);
    }

    // Set Listeners
    private void setListeners() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final BookingDetail_Fragment bookingFragment = new BookingDetail_Fragment();
        final Bundle bundle = new Bundle();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                booking = bookings.get(position);

                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Edit);
                bundle.putInt(Utils.Bundle_ID, booking.getBookingId());
                bookingFragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, bookingFragment, Utils.BookingDetail_Fragment)
                        .commit();
            }
        });

        // Change to booking detail
        newBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Insert);
                bookingFragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, bookingFragment, Utils.BookingDetail_Fragment)
                        .commit();
            }
        });

        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                // Add service
                task = Utils.Task.Submit;

                doSubmission(getString(R.string.confirmation_logout),
                        Utils.Task.Submit.toString());
                break;
        }
    }

    public void logout() {
        Intent myIntent = new Intent(getActivity(), MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }

    private void doSubmission(String message, final String taskSelection) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirmation))
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                taskSelection);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    // Show logged-in user name
    private void showUser() {
        clientDb = new ClientDbHelper(getContext());
        welcomeUser.setText(String.format("%1$s %2$s", getString(R.string.welcome), clientDb.getClientName()));
    }

    // Refresh and download all the upcoming bookings from server
    private void refreshUpcomingBookingListData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the upcoming bookings from server
    private void downloadUpcomingBookings() {
        XMLParser parser = new XMLParser();
        ClientDbHelper clientDb = new ClientDbHelper(getContext());
        String url = String.format("%1$s%2$s%3$s?id=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_CLIENT_ALL_UPCOMINGBOOKINGS, clientDb.getClientId());
        String xml = parser.getXmlFromUrl(url, true, null, null);
        bookings = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                UpcomingBooking booking;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    booking = new UpcomingBooking();

                    // Adding each child node to booking object
                    booking.setBookingId(Integer.parseInt(parser.getValue(e1, XML_NODE_BOOKING_ID)));
                    booking.setBookingTime(parser.getValue(e1, XML_NODE_BOOKING_TIME));
                    booking.setCompanyName(parser.getValue(e1, XML_NODE_COMPANY_NAME));
                    booking.setStaffName(parser.getValue(e1, XML_NODE_STAFF_NAME));

                    bookings.add(booking);
                }

                db = new UpcomingBookingDbHelper(getContext());
                db.deleteAllUpcomingBookings();
                db.addAllUpcomingBookings(bookings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bookings = db.getAllUpcomingBookings();
    }



    // Download all the active companies from server
    private void downloadCompanies() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_COMPANIES);
        String xml = parser.getXmlFromUrl(url, true, null, null);

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Company company;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    company = new Company();

                    // Adding each child node to service object
                    company.setCompanyId(Integer.parseInt(parser.getValue(e1, XML_NODE_COMPANY_ID)));
                    company.setCompanyName(parser.getValue(e1, XML_NODE_COMPANY_NAME));

                    companies.add(company);
                }

                companyDb = new CompanyDbHelper(getContext());
                companyDb.deleteAllCompanies();
                companyDb.addAllCompanies(companies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Download all the active staffs from server
    private void downloadStaffs() {
        XMLParser parser = new XMLParser();
        clientDb = new ClientDbHelper(getContext());
        String url = String.format("%1$s%2$s%3$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_STAFFS);
        String xml = parser.getXmlFromUrl(url, true, null, null);
        staffs = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Staff staff;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    staff = new Staff();

                    // Adding each child node to staff object
                    staff.setStaffId(Integer.parseInt(parser.getValue(e1, XML_NODE_STAFF_ID)));
                    staff.setStaffName(parser.getValue(e1, XML_NODE_STAFF_NAME));
                    staff.setStaffCompanyID(Integer.parseInt(parser.getValue(e1, XML_NODE_STAFF_COMPANY)));

                    staffs.add(staff);
                }

                staffDb = new StaffDbHelper(getContext());
                staffDb.deleteAllStaffs();
                staffDb.addAllStaffs(staffs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Download all the active services from server
    private void downloadServices() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_SERVICES);
        String xml = parser.getXmlFromUrl(url, true, null, null);
        services = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Service service;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    service = new Service();

                    // Adding each child node to service object
                    service.setServiceId(Integer.parseInt(parser.getValue(e1, XML_NODE_SERVICE_ID)));
                    service.setServiceName(parser.getValue(e1, XML_NODE_SERVICE_NAME));
                    service.setServicePrice(Float.parseFloat(parser.getValue(e1, XML_NODE_SERVICE_PRICE)));
                    service.setServiceDuration(Integer.parseInt(parser.getValue(e1, XML_NODE_SERVICE_DURATION)));

                    services.add(service);
                }

                serviceDb = new ServiceDbHelper(getContext());
                serviceDb.deleteAllServices();
                serviceDb.addAllServices(services);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            if (Utils.isNetworkAvailable(getContext())) {
                if (param.length != 0) {
                    if (param[0].equals(Utils.Task.Submit.toString())) {
                        logout();
                    }
                } else {
                    downloadUpcomingBookings();
                    downloadCompanies();
                    downloadStaffs();
                    downloadServices();
                }
            } else {
                db = new UpcomingBookingDbHelper(getContext());
                bookings = db.getAllUpcomingBookings();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);
            UpcomingBookingsAdapter adapter = new UpcomingBookingsAdapter(getActivity(), bookings);
            listView.setAdapter(adapter);
        }
    }

}
