package meghna.t.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import meghna.t.bookhub.R
import meghna.t.bookhub.adaper.DashboardRecyclerAdapter
import meghna.t.bookhub.model.Book
import meghna.t.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DashboardFragment : Fragment() {

//    val bookInfoList = arrayListOf<Book>(
//        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
//        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
//        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
//        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
//        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
//        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
//        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
//        Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
//        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
//        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
//    )


    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    lateinit var btnCheckInternet: Button

    var bookInfoList: ArrayList<Book> = ArrayList<Book>()

    val ratingComparator = Comparator<Book> { book1, book2 ->
        if(book1.bookRating.compareTo(book2.bookRating,true) == 0){
            //Sort according to name if rating is same
            book1.bookName.compareTo(book2.bookName,true)
        }else{
            book1.bookRating.compareTo(book2.bookRating,true)
        }
    }
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

//        btnCheckInternet = view.findViewById(R.id.btnCheckInternet)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

//        btnCheckInternet.setOnClickListener {
//            if (ConnectionManager().connectivityManager(activity as Context)) {
//                //Internet is available
//                val dialog = AlertDialog.Builder(activity as Context)
//                dialog.setTitle("Sucess")
//                dialog.setMessage("Internet Connection Found")
//                dialog.setPositiveButton("Ok") { text, listener -> }
//                dialog.setNegativeButton("Cancel") { text, listener -> }
//                dialog.create()
//                dialog.show()
//            } else {
//                //internet is not available
//                val dialog = AlertDialog.Builder(activity as Context)
//                dialog.setTitle("Error")
//                dialog.setMessage("Internet Connection is not Found")
//                dialog.setPositiveButton("Ok") { text, listener -> }
//                dialog.setNegativeButton("Cancel") { text, listener -> }
//                dialog.create()
//                dialog.show()
//            }
//
//        }
        layoutManager = LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v1/book/fetch_books"

        if (ConnectionManager().connectivityManager(activity as Context)) {


            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    //here we will handle the response
                    // println("response is $it")
                    try {
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                            }
                            recyclerAdapter =
                                DashboardRecyclerAdapter(activity as Context, bookInfoList)
                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager

//                            recyclerDashboard.addItemDecoration(
//                                DividerItemDecoration(
//                                    recyclerDashboard.context,
//                                    (layoutManager as LinearLayoutManager).orientation
//                                )
//                            )
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error has Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error has occurred",
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                }, Response.ErrorListener {
                    //here we will handle the error
                    //println("Error is $it")
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred !!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Context-type"] = "application/json"
                        headers["token"] = "f38d650ba4002e"
                        return headers
                    }

                }
            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Setting") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()

        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if(id == R.id.action_sort)
        {
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()//as sort will give lowest rating on the top
        }
        recyclerAdapter.notifyDataSetChanged()
        //notify listener abt changes


        return super.onOptionsItemSelected(item)
    }
}