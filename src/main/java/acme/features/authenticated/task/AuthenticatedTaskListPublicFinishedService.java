package acme.features.authenticated.task;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Authenticated;
import acme.framework.entities.Task;
import acme.framework.services.AbstractListService;

@Service
public class AuthenticatedTaskListPublicFinishedService implements AbstractListService<Authenticated, Task> {

	// Internal state ---------------------------------------------------------

		@Autowired
		protected AuthenticatedTaskRepository repository;

	// AbstractListService<Authenticated, Task> interface --------------
		
		@Override
		public boolean authorise(final Request<Task> request) {
			assert request != null;
			
			return true;
		}
		
		@Override
		public void unbind(final Request<Task> request, final Task entity, final Model model) {
			assert request != null;
			assert entity != null;
			assert model != null;

			request.unbind(entity, model, "title", "beginning", "ending", "workload", "description");
		}
		
		@Override
		public Collection<Task> findMany(final Request<Task> request) {
			assert request != null;
			
			Collection<Task> result;
			
			Date date;
			date = Calendar.getInstance().getTime();
			
			result = this.repository.findFinishedPublicTask(date);
			return result;
		}
}