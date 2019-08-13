package com.camero

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.*
import android.widget.Toast
import android.view.View
import android.widget.EditText
import com.developer.filepicker.model.DialogConfigs
import com.developer.filepicker.model.DialogProperties
import com.developer.filepicker.view.FilePickerDialog
import kotlinx.android.synthetic.main.dialog.view.*
import java.io.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private var mPhones: ArrayList<String>? = null

    private var mName: ArrayList<String>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var sms: String
    private val pickFileRequest: Int? = 10
    private var fb = 0
    private lateinit var floatingButton: FloatingActionButton
    private var properties = DialogProperties()
    private lateinit var dialog: FilePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initState()
    }

    private fun initState() {
        setupView()
        requestPerms()
        check()
    }

    private fun setupView() {
        mName = ArrayList()
        mPhones = ArrayList()
        sms = ""
        floatingButton = fab
        recyclerView = rv
        adapter = MyAdapter(this, this.mName!!, this.mPhones!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dialog = FilePickerDialog(this, properties)
        properties.selection_mode = DialogConfigs.SINGLE_MODE
        properties.selection_type = DialogConfigs.FILE_SELECT
        properties.root = File(DialogConfigs.DEFAULT_DIR)
        properties.error_dir = File(DialogConfigs.DEFAULT_DIR)
        properties.offset = File(DialogConfigs.DEFAULT_DIR)
        properties.extensions = null

        dialog.setTitle("Select a File")
        dialog.setDialogSelectionListener {

            if (it[0].toString().toLowerCase().contains(".csv")) {
                try {
                    readFileData(it[0])
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(applicationContext, "File is not csv", Toast.LENGTH_SHORT).show()
            }

        }




        fab.setOnClickListener {
            if (fb == 0) {
                dialog.show()
            } else {
                val view = layoutInflater.inflate(R.layout.dialog, null)
                val dialogM = AlertDialog.Builder(this)
                    .setTitle("Type Message")
                    .setView(view)
                    .setPositiveButton("OK") { _, _ ->
                        val editText = view.messageEditText
                        sendSMS(message = editText.text.toString())
                    }
                    .setNegativeButton("Cancel", null).create()
                dialogM.show()
            }
        }

    }

    private fun sendSMS(message: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$sms"))
        // message = message.replace("%s", StoresMessage.m_storeName);
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }

    @SuppressLint("PrivateResource")
    private fun check() {

        if (adapter.itemCount < 1) {
            CONTENT.visibility = View.GONE
            EMPTY.visibility = View.VISIBLE
        } else {
            this.fb = 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fab.setImageDrawable(resources.getDrawable(R.drawable.send, this.theme))
            } else {
                fab.setImageDrawable(resources.getDrawable(R.drawable.send))
            }
            CONTENT.visibility = View.VISIBLE
            EMPTY.visibility = View.GONE

        }

    }

    private fun requestPerms() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyHasPermission()) {
                requestForSpecificPermission()
            }
        }
    }

    private fun requestForSpecificPermission() {
        requestPermissions(
            this,
            arrayOf(
                RECEIVE_SMS,
                READ_SMS,
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
            ),
            101
        )
    }

    private fun checkIfAlreadyHasPermission(): Boolean {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, GET_ACCOUNTS))
    }

    @Throws(IOException::class)
    private fun readFileData(path: String) {

        var data: Array<String>

        val file = File(path)
        println("readFileData: $path ")

        if (file.exists()) {

            val fis = FileInputStream(file)
            val br = BufferedReader(InputStreamReader(fis))

            var line: String? = br.readLine()
            while (line != null) {
                data = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                println(line)
                try {
                    mName?.add(data[0])
                    mPhones?.add(data[1])

                    sms = sms + data[1] + ";"
                    adapter.notifyDataSetChanged()
                    check()

                } catch (e: Exception) {
                    println(e.toString())
                }
                line = br.readLine()

            }
            if (mName?.get(0)?.toLowerCase() ?: "" == "name") {
                mName?.removeAt(0)
                mPhones?.removeAt(0)
            }

        } else {
            Toast.makeText(applicationContext, "file not exists", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Show dialog if the read permission has been granted.
                dialog.show()

            } else {
                //Permission has not been granted. Notify the user.
                Toast.makeText(this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
