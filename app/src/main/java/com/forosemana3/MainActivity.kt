package com.forosemana3
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var listViewTasks: ListView
    private lateinit var taskList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance().getReference("tasks")
        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        listViewTasks = findViewById(R.id.listViewTasks)
        taskList = mutableListOf()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        listViewTasks.adapter = adapter

        buttonAdd.setOnClickListener {
            val task = editTextTask.text.toString().trim()
            if (task.isNotEmpty()) {
                val taskId = database.push().key
                if (taskId != null) {
                    database.child(taskId).setValue(task).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Tarea agregada", Toast.LENGTH_SHORT).show()
                            editTextTask.text.clear()
                        } else {
                            Toast.makeText(this, "Error al agregar tarea", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(String::class.java)
                    task?.let { taskList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to read value.", error.toException())
                Toast.makeText(this@MainActivity, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
