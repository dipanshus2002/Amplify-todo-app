package com.example.myamplifyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Todo

class TodoAdapter(
    private val context: Context,
    private val todos: List<Todo>,
    private val deleteListener: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTodoName: TextView = view.findViewById(R.id.tvTodoName)
        val btnDeleteTodo: ImageButton = view.findViewById(R.id.btnDeleteTodo)

        fun bind(todo: Todo) {
            tvTodoName.text = todo.name
            btnDeleteTodo.setOnClickListener { deleteListener(todo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}