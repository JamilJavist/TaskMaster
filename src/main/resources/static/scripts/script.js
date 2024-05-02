// Допустим, у вас есть массив проектов
const projects = [
{ title: "Проект 1", description: "Описание проекта 1", urgency: "Средняя", duration: "7 дней", date: "10.05.2022" },
{ title: "Проект 2", description: "Описание проекта 2", urgency: "Высокая", duration: "3 дня", date: "15.05.2022" },
{ title: "Проект 3", description: "Описание проекта 3", urgency: "Низкая", duration: "10 дней", date: "20.05.2022" },
{ title: "Проект 4", description: "Описание проекта 4", urgency: "Низкая", duration: "10 дней", date: "20.05.2022" },
{ title: "Проект 5", description: "Описание проекта 5", urgency: "Низкая", duration: "10 дней", date: "20.05.2022" },
{ title: "Проект 6", description: "Описание проекта 6", urgency: "Низкая", duration: "10 дней", date: "20.05.2022" },
];

const projectsTable = document.querySelector('.projects-table');

projects.forEach(project => {
const card = document.createElement('div');
card.classList.add('project-card');
card.innerHTML = `
<h2>${project.title}</h2>
<p>${project.description}</p>
<p>Срочность: ${project.urgency}</p>
<p>Время выполнения: ${project.duration}</p>
<p>Дата создания: ${project.date}
`;
projectsTable.appendChild(card);
});