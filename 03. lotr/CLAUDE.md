# CLAUDE.md - Agent Guidelines

## Build & Test Commands
- Build: `mvn clean compile`
- Run tests: `mvn test`
- Run single test: `mvn test -Dtest=InventoryManagerTest#testAddItem`
- Run application: `mvn exec:java -Dexec.mainClass="org.lotr.kata.LordOfTheRingsApp"`
- Check for compile errors: `mvn compile`

## Code Style Guidelines
- Java 21 features are encouraged
- Classes should use explicit visibility modifiers
- Method parameters should use meaningful names
- Format code with standard Java conventions
- Use consistent naming: camelCase for methods/variables, PascalCase for classes
- Add JavaDoc for public methods and classes
- Use domain terminology (from Lord of the Rings)
- Handle exceptions appropriately with try/catch

## Project Structure
- Follow package structure org.lotr.kata
- Apply refactoring patterns to improve code quality
- Create unit tests for all new functionality
- Follow SOLID principles when refactoring