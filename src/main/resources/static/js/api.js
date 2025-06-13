// API Configuration
console.log('API script loading...');

// Check if required functions exist
if (typeof renderPortfolioItems !== 'function') {
    console.error('renderPortfolioItems function not found. Make sure script.js is loaded after api.js');
}

const API_BASE_URL = 'http://localhost:8081/api';

// API Endpoints
const ENDPOINTS = {
    CONTACT: `${API_BASE_URL}/contact`,
    APPLY: `${API_BASE_URL}/job-applications`,
    PORTFOLIO: `${API_BASE_URL}/portfolio`,
    CAREERS: `${API_BASE_URL}/careers`
};

// API Utility Functions
const api = {
    async post(endpoint, data) {
        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    async get(endpoint) {
        try {
            const response = await fetch(endpoint);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }
};

// Contact Form Handler
async function handleContactSubmit(event) {
    event.preventDefault();
    
    const form = event.target;
    const submitBtn = form.querySelector('.submit-btn');
    const formData = {
        name: form.querySelector('#name').value,
        email: form.querySelector('#email').value,
        subject: form.querySelector('#subject').value,
        message: form.querySelector('#message').value
    };

    try {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Sending...';
        
        const response = await api.post(ENDPOINTS.CONTACT, formData);
        
        // Show success message
        alert('Thank you for your message! We will get back to you soon.');
        form.reset();
    } catch (error) {
        alert('Sorry, there was an error sending your message. Please try again later.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Send Message';
    }
}

// Job Application Handler
async function handleJobApplication(event) {
    event.preventDefault();
    
    const form = event.target;
    const submitBtn = form.querySelector('.submit-btn');
    const resumeFile = form.querySelector('#applicantResume').files[0];
    
    // Validate form data before submission
    const fullName = form.querySelector('#applicantName').value.trim();
    const email = form.querySelector('#applicantEmail').value.trim();
    const phoneNumber = form.querySelector('#applicantPhone').value.trim();
    const currentLocation = form.querySelector('#applicantLocation').value.trim();
    const yearsOfExperience = parseInt(form.querySelector('#applicantExperience').value);
    const aboutYourself = form.querySelector('#applicantMessage').value.trim();
    const position = document.getElementById('jobTitle').textContent.trim();

    // Client-side validation
    const errors = [];
    if (!fullName) errors.push('Full name is required');
    if (!email) errors.push('Email is required');
    if (!phoneNumber) errors.push('Phone number is required');
    if (!currentLocation) errors.push('Current location is required');
    if (isNaN(yearsOfExperience) || yearsOfExperience < 0) errors.push('Valid years of experience is required');
    if (!aboutYourself) errors.push('About yourself is required');
    if (!resumeFile) errors.push('Resume file is required');

    if (errors.length > 0) {
        alert('Please correct the following errors:\n' + errors.join('\n'));
        return;
    }

    // Create application data object
    const applicationData = {
        fullName,
        email,
        phoneNumber,
        currentLocation,
        yearsOfExperience,
        aboutYourself,
        position
    };

    let applicationId = null;
    let applicationSubmitted = false;

    try {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Submitting...';
        
        // First submit the application data
        console.log('Submitting application data:', { ...applicationData, resumeFile: resumeFile ? resumeFile.name : 'none' });
        
        const response = await fetch(ENDPOINTS.APPLY, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(applicationData)
        });
        
        const responseData = await response.json();
        console.log('Application submission response:', responseData);
        
        if (!response.ok) {
            // Handle validation errors from the server
            if (response.status === 400) {
                if (typeof responseData === 'object') {
                    // If the server returns an object with field-specific errors
                    const errorMessages = Object.entries(responseData)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join('\n');
                    throw new Error('Validation failed:\n' + errorMessages);
                } else if (typeof responseData === 'string') {
                    // If the server returns a simple error message
                    throw new Error(responseData);
                }
            }
            throw new Error(responseData.error || `Server error: ${response.status}`);
        }
        
        // Check if the application was actually created successfully
        if (!responseData.id) {
            throw new Error('Application submission failed: No application ID received');
        }

        // Store the application ID for resume upload
        applicationId = responseData.id;
        applicationSubmitted = true;
        
        // Check for duplicate email error in the response
        if (responseData.error && responseData.error.includes('email already exists')) {
            throw new Error('An application with this email already exists. Please use a different email address.');
        }
        
        console.log('Application submitted successfully with ID:', applicationId);
        
        // Only proceed with resume upload if we have a valid application ID and resume file
        if (resumeFile && applicationId) {
            console.log('Preparing to upload resume for application ID:', applicationId);
            
            // Add a small delay before resume upload to ensure backend processing is complete
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            // Create FormData for resume upload - exactly like Postman
            const formData = new FormData();
            // Important: Use the exact field name expected by the backend ('resume')
            formData.append('resume', resumeFile, resumeFile.name); // Include filename
            formData.append('applicationId', applicationId);
            
            console.log('Uploading resume file:', {
                name: resumeFile.name,
                type: resumeFile.type,
                size: resumeFile.size,
                lastModified: new Date(resumeFile.lastModified).toISOString()
            });
            
            // Log the FormData contents for debugging
            for (let pair of formData.entries()) {
                console.log('FormData entry:', pair[0], pair[1]);
            }
            
            const resumeResponse = await fetch(`${ENDPOINTS.APPLY}/resume`, {
                method: 'POST',
                // Important: Don't set Content-Type header - let the browser set it with the boundary
                body: formData
            });

            // Log the raw response for debugging
            console.log('Resume upload response status:', resumeResponse.status);
            console.log('Resume upload response headers:', Object.fromEntries(resumeResponse.headers.entries()));

            const responseText = await resumeResponse.text();
            console.log('Raw resume upload response:', responseText);

            if (!resumeResponse.ok) {
                throw new Error(`Resume upload failed: ${responseText}`);
            }

            // Try to parse as JSON if possible
            let resumeData;
            try {
                resumeData = JSON.parse(responseText);
                console.log('Parsed resume upload response:', resumeData);
            } catch (e) {
                console.log('Response was not JSON:', responseText);
                resumeData = responseText;
            }
            
            // Show success message
            alert('Your application has been submitted successfully!');
            form.reset();
            closeApplicationModal();
        } else {
            throw new Error('Missing resume file or application ID');
        }
    } catch (error) {
        console.error('Error during application submission:', error);
        
        if (applicationSubmitted) {
            // If the application was submitted but resume upload failed
            alert(`Your application was submitted successfully, but we were unable to upload your resume. Please contact support with your application ID: ${applicationId}`);
        } else {
            // If the application submission itself failed
            alert(error.message || 'An error occurred while submitting your application. Please try again.');
        }
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Submit Application';
    }
}

// Portfolio Data Handler
async function loadPortfolioData() {
    console.log('Starting to load portfolio data...');
    const portfolioGrid = document.querySelector('.portfolio-grid');
    console.log('Portfolio grid element found:', !!portfolioGrid);
    
    try {
        console.log('Fetching portfolio data from:', ENDPOINTS.PORTFOLIO);
        const portfolioData = await api.get(ENDPOINTS.PORTFOLIO);
        console.log('Received portfolio data:', portfolioData);
        
        if (!Array.isArray(portfolioData)) {
            console.error('Portfolio data is not an array:', portfolioData);
            throw new Error('Invalid portfolio data format');
        }
        
        if (portfolioData.length === 0) {
            console.warn('No portfolio items received');
        } else {
            console.log(`Received ${portfolioData.length} portfolio items`);
            console.log('First item sample:', portfolioData[0]);
        }
        
        if (typeof renderPortfolioItems !== 'function') {
            console.error('renderPortfolioItems is not a function:', renderPortfolioItems);
            throw new Error('renderPortfolioItems function not found');
        }
        
        console.log('Calling renderPortfolioItems with data...');
        renderPortfolioItems(portfolioData);
        console.log('Portfolio items rendered successfully');
        
        // Remove loading class after successful render
        if (portfolioGrid) {
            portfolioGrid.classList.remove('loading');
        }
    } catch (error) {
        console.error('Error loading portfolio data:', error);
        // Show error state in portfolio section
        if (portfolioGrid) {
            portfolioGrid.classList.remove('loading');
            portfolioGrid.innerHTML = 
                '<div class="error-message">Unable to load portfolio items. Please try again later.</div>';
        } else {
            console.error('Portfolio grid element not found');
        }
    }
}

// Career Listings Handler
async function loadCareerListings() {
    console.log('Starting to load career listings...');
    const jobsGrid = document.querySelector('.jobs-grid');
    console.log('Jobs grid element found:', !!jobsGrid);
    
    if (!jobsGrid) {
        console.error('Jobs grid element not found');
        return;
    }

    try {
        console.log('Fetching career data from:', ENDPOINTS.CAREERS);
        const careerData = await api.get(ENDPOINTS.CAREERS);
        console.log('Received career data:', careerData);
        
        if (!Array.isArray(careerData)) {
            console.error('Career data is not an array:', careerData);
            throw new Error('Invalid career data format');
        }
        
        if (careerData.length === 0) {
            console.warn('No career listings received');
        } else {
            console.log(`Received ${careerData.length} career listings`);
            console.log('First listing sample:', careerData[0]);
        }
        
        if (typeof renderCareerListings !== 'function') {
            console.error('renderCareerListings is not a function:', renderCareerListings);
            throw new Error('renderCareerListings function not found');
        }
        
        console.log('Calling renderCareerListings with data...');
        renderCareerListings(careerData);
        console.log('Career listings rendered successfully');
        
        // Remove loading class after successful render
        jobsGrid.classList.remove('loading');
    } catch (error) {
        console.error('Error loading career listings:', error);
        // Show error state in careers section
        jobsGrid.innerHTML = '<div class="error-message">Unable to load career listings. Please try again later.</div>';
        // Remove loading class even if there's an error
        jobsGrid.classList.remove('loading');
    }
}

// Initialize API Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM Content Loaded - Initializing API handlers...');
    
    // Contact form
    const contactForm = document.getElementById('contactForm');
    if (contactForm) {
        console.log('Contact form found, adding submit handler');
        contactForm.addEventListener('submit', handleContactSubmit);
    } else {
        console.warn('Contact form not found');
    }

    // Job application form
    const applicationForm = document.getElementById('applicationForm');
    if (applicationForm) {
        console.log('Application form found, adding submit handler');
        applicationForm.addEventListener('submit', handleJobApplication);
    } else {
        console.warn('Application form not found');
    }

    // Load dynamic data
    console.log('Starting to load dynamic data...');
    try {
        loadPortfolioData();
        loadCareerListings();
    } catch (error) {
        console.error('Error loading dynamic data:', error);
    }
});

// Export API functions for use in other scripts
window.api = api; 