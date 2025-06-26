package com.example.personaltasks.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltasks.R
import com.example.personaltasks.data.Task

/**
 * Adapter responsável por exibir a lista de tarefas no RecyclerView
 * e tratar o clique longo que exibe o menu de contexto.
 */
class TaskAdapter(
    private val tasks: List<Task>, // Lista de tarefas a ser exibida
    private val onLongClick: (Task, View) -> Unit // Função lambda executada ao clicar e segurar em um item
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    /**
     * ViewHolder representa cada item visual da lista (1 tarefa)
     */
    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val status: TextView = view.findViewById(R.id.txtStatus)
        val title: TextView = view.findViewById(R.id.txtTitle)
        val description: TextView = view.findViewById(R.id.txtDescription)
        val deadline: TextView = view.findViewById(R.id.txtDeadline)

        init {
            // Listener para clique longo — usado para abrir o menu de contexto
            view.setOnLongClickListener {
                onLongClick(tasks[adapterPosition], it)
                true
            }
        }
    }

    /**
     * Cria uma nova ViewHolder inflando o layout item_task.xml
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    /**
     * Associa os dados da tarefa com os elementos visuais (TextViews)
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.description.text = task.description
        holder.deadline.text = task.deadline
        holder.status.text = if (task.completed) "Cumprida ✅" else "Pendente ⏳"
    }


    /**
     * Retorna o total de itens na lista de tarefas
     */
    override fun getItemCount(): Int = tasks.size
    
    
}
