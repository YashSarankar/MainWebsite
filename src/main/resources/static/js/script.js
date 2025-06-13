console.log('Script.js loading...');

// Mobile Navigation
const navLinks = document.querySelector('.nav-links');
const navLinksItems = document.querySelectorAll('.nav-links a');

// Smooth scroll for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const href = this.getAttribute('href');
        // Skip if href is just "#"
        if (href === '#') return;
        
        const target = document.querySelector(href);
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Navigation and UI Functions
let lastScrollTop = 0;
const navbar = document.querySelector('.navbar');

// Scroll handler for navbar
window.addEventListener('scroll', () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    
    if (scrollTop > lastScrollTop && scrollTop > 100) {
        navbar.classList.add('scroll-down');
        navbar.classList.remove('scroll-up');
    } else {
        navbar.classList.remove('scroll-down');
        navbar.classList.add('scroll-up');
    }
    
    lastScrollTop = scrollTop;
});

// Set active navigation item
function setActiveNavItem() {
    const sections = document.querySelectorAll('section');
    const navLinks = document.querySelectorAll('.nav-links a');
    
    sections.forEach(section => {
        const rect = section.getBoundingClientRect();
        if (rect.top <= 100 && rect.bottom >= 100) {
            const id = section.getAttribute('id');
            navLinks.forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('href') === `#${id}`) {
                    link.classList.add('active');
                }
            });
        }
    });
}

window.addEventListener('scroll', setActiveNavItem);

// Form submission handling
const contactForm = document.getElementById('contactForm');

contactForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    // Get form values
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const subject = document.getElementById('subject').value;
    const message = document.getElementById('message').value;
    
    // Basic form validation
    if (!name || !email || !subject || !message) {
        alert('Please fill in all fields');
        return;
    }
    
    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('Please enter a valid email address');
        return;
    }
    
    // Here you would typically send the form data to a server
    // For now, we'll just show a success message
    alert('Thank you for your message! We will get back to you soon.');
    contactForm.reset();
});

// Portfolio variables - declare once at the top
const portfolioGrid = document.querySelector('.portfolio-grid');
const portfolioItems = document.querySelectorAll('.portfolio-item');
const filterButtons = document.querySelectorAll('.filter-btn');

// Add loading animation to service cards
const serviceCards = document.querySelectorAll('.service-card');
serviceCards.forEach((card, index) => {
    card.style.animationDelay = `${index * 0.1}s`;
});

// Portfolio Filter Functions
function initializePortfolioFilters() {
    const filterButtons = document.querySelectorAll('.filter-btn');
    const portfolioItems = document.querySelectorAll('.portfolio-item');
    
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove active class from all buttons
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Add active class to clicked button
            button.classList.add('active');
            
            const filter = button.getAttribute('data-filter');
            
            portfolioItems.forEach(item => {
                if (filter === 'all' || item.getAttribute('data-category') === filter) {
                    item.style.display = 'block';
                    setTimeout(() => item.style.opacity = '1', 10);
                } else {
                    item.style.opacity = '0';
                    setTimeout(() => item.style.display = 'none', 300);
                }
            });
        });
    });
}

// Portfolio Item Rendering
function renderPortfolioItems(data) {
    console.log('renderPortfolioItems called with data:', data);
    const portfolioGrid = document.querySelector('.portfolio-grid');
    console.log('Portfolio grid element found:', !!portfolioGrid);
    
    if (!portfolioGrid) {
        console.error('Portfolio grid element not found');
        return;
    }

    if (!Array.isArray(data)) {
        console.error('Invalid data format: expected array, got', typeof data);
        portfolioGrid.innerHTML = '<div class="error-message">Invalid data format received</div>';
        return;
    }

    try {
        console.log('Generating HTML for portfolio items...');
        const html = data.map(item => {
            console.log('Processing item:', item.title);
            return `
                <div class="portfolio-item" data-category="${item.category}">
                    <div class="portfolio-image">
                        <img src="${item.image}" alt="${item.title}" onerror="this.src='images/placeholder.jpg'">
                    </div>
                    <div class="portfolio-overlay">
                        <button class="close-overlay" aria-label="Close overlay">
                            <i class="fas fa-times"></i>
                        </button>
                        <div class="portfolio-info">
                            <span class="portfolio-category">${item.category}</span>
                            <h3>${item.title}</h3>
                            <p>${item.description}</p>
                            <div class="portfolio-tech">
                                ${item.technologies.map(tech => `<span>${tech}</span>`).join('')}
                            </div>
                            <div class="portfolio-links">
                                ${item.liveUrl ? `
                                    <a href="${item.liveUrl}" class="portfolio-link" target="_blank" rel="noopener">
                                        <i class="fas fa-external-link-alt"></i>
                                    </a>
                                ` : ''}
                                ${item.githubUrl ? `
                                    <a href="${item.githubUrl}" class="portfolio-link" target="_blank" rel="noopener">
                                        <i class="fab fa-github"></i>
                                    </a>
                                ` : ''}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        console.log('Setting portfolio grid HTML...');
        portfolioGrid.innerHTML = html;
        console.log('Portfolio grid HTML set successfully');

        // Remove loading class
        portfolioGrid.classList.remove('loading');

        // Reinitialize portfolio filters after rendering
        console.log('Reinitializing portfolio filters...');
        initializePortfolioFilters();
        console.log('Portfolio filters initialized');

        // Initialize portfolio overlays
        console.log('Initializing portfolio overlays...');
        initializePortfolioOverlays();
        console.log('Portfolio overlays initialized');
    } catch (error) {
        console.error('Error rendering portfolio items:', error);
        portfolioGrid.innerHTML = '<div class="error-message">Error rendering portfolio items</div>';
        portfolioGrid.classList.remove('loading');
    }
}

// Career Listings Rendering
function renderCareerListings(data) {
    const jobsGrid = document.querySelector('.jobs-grid');
    if (!jobsGrid) return;

    jobsGrid.innerHTML = data.map(job => `
        <div class="job-card" data-category="${job.category}">
            <div class="job-header">
                <h3>${job.title}</h3>
                <span class="job-type">${job.type}</span>
            </div>
            <div class="job-details">
                <div class="job-tag">
                    <i class="fas fa-laptop-code"></i>
                    ${job.category}
                </div>
                <div class="job-tag">
                    <i class="fas fa-map-marker-alt"></i>
                    ${job.location}
                </div>
                <div class="job-tag">
                    <i class="fas fa-clock"></i>
                    ${job.duration}
                </div>
            </div>
            <p class="job-description">${job.description}</p>
            <div class="job-skills">
                ${job.skills.map(skill => `<span>${skill}</span>`).join('')}
            </div>
            <button class="apply-btn" data-job="${job.title}">Apply Now</button>
        </div>
    `).join('');

    // Reinitialize job application handlers
    initializeJobApplicationHandlers();
}

// Job Application Modal Functions
function initializeJobApplicationHandlers() {
    const applyButtons = document.querySelectorAll('.apply-btn');
    const modal = document.getElementById('applicationModal');
    const closeModal = document.querySelector('.close-modal');
    const jobTitle = document.getElementById('jobTitle');

    applyButtons.forEach(button => {
        button.addEventListener('click', () => {
            const position = button.getAttribute('data-job');
            jobTitle.textContent = position;
            modal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        });
    });

    closeModal.addEventListener('click', closeApplicationModal);

    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            closeApplicationModal();
        }
    });
}

function closeApplicationModal() {
    const modal = document.getElementById('applicationModal');
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
}

// Portfolio Overlay Functions
function initializePortfolioOverlays() {
    const portfolioItems = document.querySelectorAll('.portfolio-item');
    const closeButtons = document.querySelectorAll('.close-overlay');

    portfolioItems.forEach(item => {
        item.addEventListener('click', (e) => {
            if (!e.target.closest('.close-overlay')) {
                const overlay = item.querySelector('.portfolio-overlay');
                overlay.style.opacity = '1';
            }
        });
    });

    closeButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.stopPropagation();
            const overlay = button.closest('.portfolio-overlay');
            overlay.style.opacity = '0';
        });
    });
}

// Initialize all functionality
document.addEventListener('DOMContentLoaded', () => {
    setActiveNavItem();
    initializePortfolioFilters();
    initializePortfolioOverlays();
    initializeJobApplicationHandlers();
});

// Intersection Observer for fade-in animations
const observerOptions = {
    root: null,
    rootMargin: '0px',
    threshold: 0.1
};

const observer = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('fade-in');
            observer.unobserve(entry.target);
        }
    });
}, observerOptions);

// Observe all sections
document.querySelectorAll('section').forEach(section => {
    observer.observe(section);
});

// Careers Section Functionality
document.addEventListener('DOMContentLoaded', function() {
    // Modal handling
    const applyButtons = document.querySelectorAll('.apply-btn');
    const modal = document.getElementById('applicationModal');
    const closeModal = document.querySelector('.close-modal');
    const applicationForm = document.getElementById('applicationForm');
    const jobTitleSpan = document.getElementById('jobTitle');
    const fileInput = document.getElementById('applicantResume');
    const fileLabel = document.querySelector('.file-label');

    // Open modal when apply button is clicked
    applyButtons.forEach(button => {
        button.addEventListener('click', function() {
            const jobTitle = this.getAttribute('data-job');
            if (jobTitleSpan) {
                jobTitleSpan.textContent = jobTitle;
            }
            if (modal) {
                modal.style.display = 'block';
                document.body.style.overflow = 'hidden'; // Prevent background scrolling
            }
        });
    });

    // Close modal
    if (closeModal) {
        closeModal.addEventListener('click', () => {
            if (modal) {
                modal.style.display = 'none';
                document.body.style.overflow = ''; // Restore scrolling
            }
            if (applicationForm) {
                applicationForm.reset();
            }
            if (fileLabel) {
                fileLabel.innerHTML = '<i class="fas fa-upload"></i> Upload Resume';
            }
        });
    }

    // Close modal when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
            document.body.style.overflow = '';
            if (applicationForm) {
                applicationForm.reset();
            }
            if (fileLabel) {
                fileLabel.innerHTML = '<i class="fas fa-upload"></i> Upload Resume';
            }
        }
    });

    // Handle file input change
    if (fileInput && fileLabel) {
        fileInput.addEventListener('change', function() {
            if (this.files.length > 0) {
                const fileName = this.files[0].name;
                fileLabel.innerHTML = `<i class="fas fa-file"></i> ${fileName}`;
            } else {
                fileLabel.innerHTML = '<i class="fas fa-upload"></i> Upload Resume';
            }
        });
    }

    // Handle form submission
    if (applicationForm) {
        applicationForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Get form data
            const formData = new FormData(this);
            
            // Here you would typically send the form data to your server
            // For now, we'll just show a success message
            alert('Thank you for your application! We will review your submission and get back to you soon.');
            
            // Reset form and close modal
            this.reset();
            if (fileLabel) {
                fileLabel.innerHTML = '<i class="fas fa-upload"></i> Upload Resume';
            }
            if (modal) {
                modal.style.display = 'none';
                document.body.style.overflow = '';
            }
        });
    }

    // Job filters functionality
    const filterButtons = document.querySelectorAll('.filter-btn');
    const jobCards = document.querySelectorAll('.job-card');

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class from all buttons
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Add active class to clicked button
            this.classList.add('active');

            const filter = this.getAttribute('data-filter');
            
            jobCards.forEach(card => {
                if (filter === 'all' || card.getAttribute('data-category') === filter) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
});

// Verify script loading
if (typeof window !== 'undefined') {
    window.renderPortfolioItems = function(data) {
        console.log('renderPortfolioItems called with data:', data);
        const portfolioGrid = document.querySelector('.portfolio-grid');
        console.log('Portfolio grid element found:', !!portfolioGrid);
        
        if (!portfolioGrid) {
            console.error('Portfolio grid element not found');
            return;
        }

        if (!Array.isArray(data)) {
            console.error('Invalid data format: expected array, got', typeof data);
            portfolioGrid.innerHTML = '<div class="error-message">Invalid data format received</div>';
            return;
        }

        try {
            console.log('Generating HTML for portfolio items...');
            const html = data.map(item => {
                console.log('Processing item:', item.title);
                return `
                    <div class="portfolio-item" data-category="${item.category}">
                        <div class="portfolio-image">
                            <img src="${item.image}" alt="${item.title}" onerror="this.src='images/placeholder.jpg'">
                        </div>
                        <div class="portfolio-overlay">
                            <button class="close-overlay" aria-label="Close overlay">
                                <i class="fas fa-times"></i>
                            </button>
                            <div class="portfolio-info">
                                <span class="portfolio-category">${item.category}</span>
                                <h3>${item.title}</h3>
                                <p>${item.description}</p>
                                <div class="portfolio-tech">
                                    ${item.technologies.map(tech => `<span>${tech}</span>`).join('')}
                                </div>
                                <div class="portfolio-links">
                                    ${item.liveUrl ? `
                                        <a href="${item.liveUrl}" class="portfolio-link" target="_blank" rel="noopener">
                                            <i class="fas fa-external-link-alt"></i>
                                        </a>
                                    ` : ''}
                                    ${item.githubUrl ? `
                                        <a href="${item.githubUrl}" class="portfolio-link" target="_blank" rel="noopener">
                                            <i class="fab fa-github"></i>
                                        </a>
                                    ` : ''}
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            }).join('');

            console.log('Setting portfolio grid HTML...');
            portfolioGrid.innerHTML = html;
            console.log('Portfolio grid HTML set successfully');

            // Remove loading class
            portfolioGrid.classList.remove('loading');

            // Reinitialize portfolio filters after rendering
            console.log('Reinitializing portfolio filters...');
            initializePortfolioFilters();
            console.log('Portfolio filters initialized');

            // Initialize portfolio overlays
            console.log('Initializing portfolio overlays...');
            initializePortfolioOverlays();
            console.log('Portfolio overlays initialized');
        } catch (error) {
            console.error('Error rendering portfolio items:', error);
            portfolioGrid.innerHTML = '<div class="error-message">Error rendering portfolio items</div>';
            portfolioGrid.classList.remove('loading');
        }
    };
} 