
package acme.features.manager.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Manager;
import acme.framework.entities.Privacy;
import acme.framework.entities.Spam;
import acme.framework.entities.Task;
import acme.framework.services.AbstractCreateService;
import acme.utilities.ValidateSpam;

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
		final Manager manager = this.repository.findManegerInSession(request.getPrincipal().getUsername());
		Date ini = null;
		Date end = null;

		try {
			ini = format.parse("2021/10/30 17:15");
			end = format.parse("2021/10/30 17:45");
		} catch (final ParseException e) {
		}
		result = new Task();
		result.setManager(manager);
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

		final boolean español = request.getLocale().toString().equals("es");

		if (!request.getModel().getString("beginning").isEmpty() && !request.getModel().getString("ending").isEmpty() && !request.getModel().getString("workload").isEmpty()) {
			final SimpleDateFormat format = !español ? new SimpleDateFormat("yyyy/MM/dd HH:mm") : new SimpleDateFormat("dd/MM/yyyy HH:mm");

			Date ini = null;
			Date end = null;
			try {
				ini = format.parse(request.getModel().getString("beginning"));
				end = format.parse(request.getModel().getString("ending"));

				final long time = end.getTime() - ini.getTime();
				final long minutes = TimeUnit.MILLISECONDS.toMinutes(time);

				String workload = request.getModel().getString("workload").replace(',', '.');
				workload = workload.contains(".") ? workload : workload.concat(".0");
				final String decimalsString = workload.substring(workload.indexOf('.') + 1);

				final Double decimals = decimalsString.length() > 1 ? Double.valueOf(decimalsString) : Double.valueOf(decimalsString + '0');
				final Double workloadMinutes = Double.valueOf(workload.substring(0, workload.indexOf('.'))) * 60 + decimals;

				final String title = request.getModel().getString("title").toLowerCase();
				final String description = request.getModel().getString("description").toLowerCase();

				final Spam spam = this.repository.getSpamWords().get(0);

				final ValidateSpam validaSpam = new ValidateSpam();

				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(ini);
				
				errors.state(request, String.valueOf(calendar.get(Calendar.YEAR)).length() == 4, "beginning", "manager.task.form.date.error");

				calendar.setTime(end);
				
				errors.state(request, String.valueOf(calendar.get(Calendar.YEAR)).length() == 4, "ending", "manager.task.form.date.error");

				errors.state(request, ini.after(new Date()), "beginning", "manager.task.form.beginning.error1");
				errors.state(request, end.after(new Date()), "ending", "manager.task.form.ending.error1");
				errors.state(request, !end.before(ini), "ending", "manager.task.form.ending.error2");
				errors.state(request, !end.equals(ini), "ending", "manager.task.form.ending.error3");
				errors.state(request, !end.equals(ini), "beginning", "manager.task.form.beginning.error2");
				errors.state(request, decimals < 60, "workload", "manager.task.form.workload.error1");
				errors.state(request, Double.valueOf(workload) > 0 && Double.valueOf(workload) < 100, "workload", "manager.task.form.workload.error2");
				errors.state(request, minutes >= workloadMinutes, "workload", "manager.task.form.workload.error3");
				errors.state(request, decimalsString.length() <= 2, "workload", "manager.task.form.workload.error4");
				errors.state(request, !validaSpam.validateSpam(title, spam), "title", "manager.task.form.title.error");
				errors.state(request, !validaSpam.validateSpam(description, spam), "description", "manager.task.form.description.error");

			} catch (final ParseException | NumberFormatException e) {
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
