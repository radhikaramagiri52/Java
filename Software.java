import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Software {

	private String name;
	private List<Software> dependencies;

	Software(String name) {
		this.name = name;
		dependencies = new ArrayList<>();
	}

	String getName() {
		return name;
	}

	List<Software> getDependencies() {
		return dependencies;
	}

	void addDependencies(Software dependency) {
		this.dependencies.add(dependency);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Software)) {
			return false;
		}
		Software software = (Software) o;
		return Objects.equals(name, software.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
