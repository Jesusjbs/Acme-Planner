
package acme.features.manager.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Manager;
import acme.framework.entities.Privacy;
import acme.framework.entities.Task;
import acme.framework.services.AbstractCreateService;

@Service
public class ManagerTaskCreateService implements AbstractCreateService<Manager, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected ManagerTaskRepository repository;

	// AbstractCreateService<Manager, Task> interface --------------


	@Override
	public boolean authorise(final Request<Task> request) {
		assert request != null;

		return true;
	}

	@Override
	public void bind(final Request<Task> request, final Task entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Task> request, final Task entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "title", "beginning", "ending", "workload", "description", "link", "privacy");
	}

	@Override
	public Task instantiate(final Request<Task> request) {
		assert request != null;

		final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		final Task result;
		Date ini = null;
		Date end = null;

		try {
			ini = format.parse("2021/06/30 17:15");
			end = format.parse("2021/06/30 17:45");
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		result = new Task();
		result.setTitle("Task 001");
		result.setBeginning(ini);
		result.setEnding(end);
		result.setWorkload(0.30);
		result.setDescription("Creation of Task entity");
		result.setLink("https://example.org");
		result.setPrivacy(Privacy.PRIVATE);

		return result;
	}

	@Override
	public void validate(final Request<Task> request, final Task entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		if (!request.getModel().getString("beginning").isEmpty() && !request.getModel().getString("ending").isEmpty() 
			&& !request.getModel().getString("workload").isEmpty()) {
			final SimpleDateFormat format = !request.getLocale().toString().equals("es") ? new SimpleDateFormat("yyyy/MM/dd HH:mm") : new SimpleDateFormat("dd/MM/yyyy HH:mm");

			Date ini = null;
			Date end = null;
			try {
				ini = format.parse(request.getModel().getString("beginning"));
				end = format.parse(request.getModel().getString("ending"));
			} catch (final ParseException e) {
			}

			final long time = end.getTime() - ini.getTime();
			final long minutes = TimeUnit.MILLISECONDS.toMinutes(time);

			String workload = request.getModel().getString("workload").replace(',', '.');
			workload = workload.contains(".") ? workload : workload.concat(".0");
			final String decimalsString = workload.substring(workload.indexOf('.') + 1);

			final Double decimals = decimalsString.length() > 1 ? Double.valueOf(decimalsString) : Double.valueOf(decimalsString + '0');
			final Double workloadMinutes = Double.valueOf(workload.substring(0, workload.indexOf('.'))) * 60 + decimals;

			if (decimals >= 60) {
				errors.add("workload", "Workload's decimals must be between 0 and 59");
			} else if (Double.valueOf(workload) < 0) {
				errors.add("workload", "Workload must be a positive");
			} else if (minutes < workloadMinutes) {
				errors.add("workload", "Workload must be between beginning and ending");
			}
		}
	}

	@Override
	public void create(final Request<Task> request, final Task entity) {
		assert request != null;
		assert entity != null;

		this.repository.save(entity);
	}

}