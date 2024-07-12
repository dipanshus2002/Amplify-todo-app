package com.example.myamplifyapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Todo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.LayoutInflater
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addTodoButton: FloatingActionButton
    private lateinit var adapter: TodoAdapter
    private val todos = mutableListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        addTodoButton = findViewById(R.id.btnAddTodo)

        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }

        adapter = TodoAdapter(this, todos) { todo ->
            deleteTodo(todo.id)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addTodoButton.setOnClickListener {
            showAddTodoDialog()
        }

        fetchTodos()
    }

    private fun showAddTodoDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_todo, null)
        val etTodoName: EditText = dialogView.findViewById(R.id.etTodoName)
        val etTodoDescription: EditText = dialogView.findViewById(R.id.etTodoDescription)

        AlertDialog.Builder(this)
            .setTitle("Add Todo")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val todoName = etTodoName.text.toString()
                val todoDescription = etTodoDescription.text.toString()
                if (todoName.isNotEmpty() && todoDescription.isNotEmpty()) {
                    val newTodo = Todo.builder()
                        .name(todoName)
                        .description(todoDescription)
                        .build()
                    createTodo(newTodo)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun createTodo(todo: Todo) {
        Amplify.API.mutate(
            com.amplifyframework.api.graphql.model.ModelMutation.create(todo),
            { response ->
                runOnUiThread {
                    todos.add(response.data)
                    adapter.notifyItemInserted(todos.size - 1)
                }
                Log.i("MyAmplifyApp", "Added Todo with id: ${response.data.id}")
            },
            { error -> Log.e("MyAmplifyApp", "Create failed", error) }
        )
    }

    private fun fetchTodos() {
        Amplify.API.query(
            com.amplifyframework.api.graphql.model.ModelQuery.list(Todo::class.java),
            { response ->
                runOnUiThread {
                    todos.clear()
                    todos.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            },
            { error -> Log.e("MyAmplifyApp", "Query failed", error) }
        )
    }

    private fun deleteTodo(id: String) {
        val todoToDelete = Todo.justId(id)
        Amplify.API.mutate(
            com.amplifyframework.api.graphql.model.ModelMutation.delete(todoToDelete),
            { response ->
                runOnUiThread {
                    val position = todos.indexOfFirst { it.id == id }
                    if (position != -1) {
                        todos.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                }
                Log.i("MyAmplifyApp", "Deleted Todo with id: ${response.data.id}")
            },
            { error -> Log.e("MyAmplifyApp", "Delete failed", error) }
        )
    }
}